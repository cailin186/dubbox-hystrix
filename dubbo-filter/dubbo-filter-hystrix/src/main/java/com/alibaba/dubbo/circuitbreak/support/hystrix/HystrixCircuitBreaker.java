package com.alibaba.dubbo.circuitbreak.support.hystrix;

import org.apache.log4j.Logger;

import com.alibaba.dubbo.circuitbreak.CircuitBreaker;
import com.alibaba.dubbo.circuitbreak.util.ProfileUtil;
import com.alibaba.dubbo.circuitbreak.util.mail.EmailBatchSendUtils;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.rpc.*;
import com.netflix.hystrix.*;

/**
 * @author cailin
 * @create 20170704 20:08
 **/
public class HystrixCircuitBreaker extends HystrixConfig implements CircuitBreaker {

	
	private Invoker<?> invoker;
	private Invocation invocation;
	private static Logger logger = Logger.getLogger(HystrixCircuitBreaker.class);

	/**
	 * 断路器配置未来可优化为动态配置，策略不写死在代码中
	 * 
	 * @param invoker
	 * @param invocation
	 */
	public HystrixCircuitBreaker(Invoker<?> invoker, Invocation invocation) {
		
	

		// 用Dubbo服务提供者接口名来定义断路器分组key
		super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(invoker.getInterface().getName()))
				// 用Dubbo服务提供者接口的方法名+方法入参个数定义相同依赖的key
				.andCommandKey(HystrixCommandKey.Factory.asKey(String.format("%s_%d", invocation.getMethodName(),
						invocation.getArguments() == null ? 0 : invocation.getArguments().length)))
				// 重写断路器基本策略属性
				.andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
						// 断路器是否开启，默认：true
						.withCircuitBreakerEnabled(getBreakerEnabled(invoker.getUrl()))
						// 统计时间滚动窗口，以毫秒为单位，默认：10秒
						.withMetricsRollingStatisticalWindowInMilliseconds(getStatisticalWindowSeconds(invoker.getUrl()))
						// 断路器在整个统计时间内是否开启的阀值，也就挥起作用是10秒钟内至少请求10次，断路器才发
						.withCircuitBreakerRequestVolumeThreshold(getThresholdValue(invoker.getUrl()))
						// 断路器默认工作时间，默认:5秒，断路器中断请求5秒后会进入半打开状态，放部分流量过去重试
						.withCircuitBreakerSleepWindowInMilliseconds(getBreakerSleepMilliseconds(invoker.getUrl()))
						// 当出错率超过30%后断路器启动，默认:50%
						.withCircuitBreakerErrorThresholdPercentage(getBreakerErrorPercent(invoker.getUrl()))
						// 是否开启Hystrix超时机制，这里禁用Hystrix的超时，使用dubbo的超时
						.withExecutionTimeoutEnabled(false))
				// 线程池配置，可考虑获取Dubbo提供者的线程数来配置断路器的线程数，若使用Hystrix的线程数则应大于Dubbo服务提供者的线程数，保证管道匹配
				.andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter().withCoreSize(getThreadPoolCoreSize(invoker.getUrl()))));

		this.invoker = invoker;
		this.invocation = invocation;
	}


	/**
	 * 熔断器发生作用时，返回错误及异常
	 * @return
	 */
	@Override
	protected Result getFallback() {
		Throwable throwable = new RpcException("Hystrix fallback");
		Result result = new RpcResult(throwable);
	   	String mailTo = ProfileUtil.getProperty("monitor.mail.to") == null ? "cailin@51talk.com" : ProfileUtil.getProperty("monitor.mail.to");
		EmailBatchSendUtils.sendMail(mailTo,"熔断报警",throwable.getStackTrace().toString());
		return result;
	}

	@Override
	public Result circuitBreak() {
		return super.execute();
	}

	@Override
	protected Result run() throws Exception {
		return invoker.invoke(invocation);
	}

	
	
	
	
}
