1.断路器在dubbo中的配置
除  
```
<dubbo:parameter key="breakerEnabled" value="true"/>
```
外，其他所有的内容都采用默认值

###1.参数配置###

2.consumer端详细的配置
```
	<dubbo:reference id="stuPointStatisticsService"  interface="com.talk51.modules.point.StuPointStatisticsService" protocol="dubbo" retries="0"
		version="3.0.3"  circuitbreak="hystrix" >
		<!--这个值最好和provider端相同-->
	      <dubbo:parameter key="threadPoolCoreSize" value="1000"/>
		  <!--开启断路器，默认为不开启-->
		  <dubbo:parameter key="breakerEnabled" value="true"/>
		  <!--统计时间滚动窗口，以毫秒为单位，默认：10秒-->
		 <dubbo:parameter key="RollingStatisticalWindowInMilliseconds" value="10000"/>
		  <!--时间窗口内的断路器阈值，满足该阈值，断路器启动，默认为10
		  断路器在整个统计时间内是否开启的阀值，也就是10秒钟内至少请求10次，断路器才发挥起作用  -->
		  <dubbo:parameter key="ThresholdValue" value="10"/>
		  <!--断路器默认工作时间，默认:5秒，断路器中断请求5秒后会进入半打开状态，放部分流量过去重试-->
          <dubbo:parameter key="breakerSleepMilliseconds" value="5000"/>
          <!-- 当出错率超过50%后断路器启动，默认:50%-->
          <dubbo:parameter key="breakerErrorPercent" value="50"/>
		
    </dubbo:reference>
```

3.断路器原理
http://www.voidcn.com/blog/t0591/article/p-6174364.html

4.简单测试代码

```
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

```

5.增加熔断后的邮件报警
采用邮件队列的形式进行邮件报警，当达到邮件队列的临界值时才会报警，若单位时间内的报警比较多，会对报警邮件进行合并
1）需要在自己的应用中添加一个mail.properties文件
```
sendcloud.url=xxx #邮件服务器地址
sendcloud.apiUser=xxx#发件人邮箱
sendcloud.apiKey=xxx #发件人密码
sendcloud.open=yes
monitor.mail.to=xxx@qq.com #收件人的逗号分隔
email.queue.size=10 #邮件队列大小
email.fixsend.time.interval=200  #发送时间间隔
```
