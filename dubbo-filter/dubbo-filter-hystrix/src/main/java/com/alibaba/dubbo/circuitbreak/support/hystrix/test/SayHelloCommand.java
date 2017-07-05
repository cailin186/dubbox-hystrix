package com.alibaba.dubbo.circuitbreak.support.hystrix.test;

import com.netflix.hystrix.*;

public class SayHelloCommand extends HystrixCommand<String> {
	private Integer timeoutInMilliseconds;
	private Integer coreSize;
	private Integer maxConcurrentRequests;

	protected SayHelloCommand(Setter setter) {
		super(setter);
	}

	/**
	 * 采用线程池来控制服务依赖
	 *
	 * @param coreSize
	 * @param timeoutInMilliseconds
	 */
	public SayHelloCommand(Integer coreSize, Integer timeoutInMilliseconds) {
		super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(SayHelloCommand.class.getName()))
				.andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey(SayHelloCommand.class.getName()))
				.andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter().withCoreSize(coreSize))// 服务线程池数量
				.andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withExecutionTimeoutEnabled(true)
						.withExecutionTimeoutInMilliseconds(timeoutInMilliseconds)// 超时时间
						.withCircuitBreakerErrorThresholdPercentage(60)// 熔断器关闭到打开阈值
						.withCircuitBreakerSleepWindowInMilliseconds(3000)));//// 熔断器打开到关闭的时间窗长度
	}

	/**
	 * 采用信号量来控制是否熔断
	 *
	 * @param Strategy
	 * @param maxConcurrentRequests
	 * @param timeoutInMilliseconds
	 */
	public SayHelloCommand(HystrixCommandProperties.ExecutionIsolationStrategy Strategy, Integer maxConcurrentRequests,
			Integer timeoutInMilliseconds) {
		super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(SayHelloCommand.class.getName()))
				.andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey(SayHelloCommand.class.getName()))
				.andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter())// 服务线程池数量
				.andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withExecutionIsolationStrategy(Strategy)// 自定义策略
						.withExecutionIsolationSemaphoreMaxConcurrentRequests(maxConcurrentRequests) // 信号量自定义最大并发数
						.withExecutionTimeoutEnabled(true).withExecutionTimeoutInMilliseconds(timeoutInMilliseconds)// 超时时间
						.withCircuitBreakerErrorThresholdPercentage(60)// 熔断器关闭到打开阈值
						.withCircuitBreakerSleepWindowInMilliseconds(3000)));//// 熔断器打开到关闭的时间窗长度
	}

	public Integer getTimeoutInMilliseconds() {
		return timeoutInMilliseconds;
	}

	public void setTimeoutInMilliseconds(Integer timeoutInMilliseconds) {
		this.timeoutInMilliseconds = timeoutInMilliseconds;
	}

	public Integer getCoreSize() {
		return coreSize;
	}

	public void setCoreSize(Integer coreSize) {
		this.coreSize = coreSize;
	}

	public Integer getMaxConcurrentRequests() {
		return maxConcurrentRequests;
	}

	public void setMaxConcurrentRequests(Integer maxConcurrentRequests) {
		this.maxConcurrentRequests = maxConcurrentRequests;
	}

	@Override
	protected String run() throws Exception {
		Thread.sleep(1001L); // 演示超时进入Fallback
		return "hello";
	}

	@Override
	protected String getFallback() {
		return String.format("FallBack");
	}

	public static void main(String[] args) {
		 SayHelloCommand command = new SayHelloCommand(20,30000);
		 String execute = command.execute();
		 System.out.println(execute);
//
//		SayHelloCommand command = new SayHelloCommand(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE, 20,30);
//		String execute = command.execute();
//		System.out.println(execute);

	}
}
