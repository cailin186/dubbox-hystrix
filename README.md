1.��·����dubbo�е�����
��  
```
<dubbo:parameter key="breakerEnabled" value="true"/>
```
�⣬�������е����ݶ�����Ĭ��ֵ

###1.��������###

2.consumer����ϸ������
```
	<dubbo:reference id="stuPointStatisticsService"  interface="com.talk51.modules.point.StuPointStatisticsService" protocol="dubbo" retries="0"
		version="3.0.3"  circuitbreak="hystrix" >
		<!--���ֵ��ú�provider����ͬ-->
	      <dubbo:parameter key="threadPoolCoreSize" value="1000"/>
		  <!--������·����Ĭ��Ϊ������-->
		  <dubbo:parameter key="breakerEnabled" value="true"/>
		  <!--ͳ��ʱ��������ڣ��Ժ���Ϊ��λ��Ĭ�ϣ�10��-->
		 <dubbo:parameter key="RollingStatisticalWindowInMilliseconds" value="10000"/>
		  <!--ʱ�䴰���ڵĶ�·����ֵ���������ֵ����·��������Ĭ��Ϊ10
		  ��·��������ͳ��ʱ�����Ƿ����ķ�ֵ��Ҳ����10��������������10�Σ���·���ŷ���������  -->
		  <dubbo:parameter key="ThresholdValue" value="10"/>
		  <!--��·��Ĭ�Ϲ���ʱ�䣬Ĭ��:5�룬��·���ж�����5���������״̬���Ų���������ȥ����-->
          <dubbo:parameter key="breakerSleepMilliseconds" value="5000"/>
          <!-- �������ʳ���50%���·��������Ĭ��:50%-->
          <dubbo:parameter key="breakerErrorPercent" value="50"/>
		
    </dubbo:reference>
```

3.��·��ԭ��
http://www.voidcn.com/blog/t0591/article/p-6174364.html

4.�򵥲��Դ���

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
	 * �����̳߳������Ʒ�������
	 *
	 * @param coreSize
	 * @param timeoutInMilliseconds
	 */
	public SayHelloCommand(Integer coreSize, Integer timeoutInMilliseconds) {
		super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(SayHelloCommand.class.getName()))
				.andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey(SayHelloCommand.class.getName()))
				.andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter().withCoreSize(coreSize))// �����̳߳�����
				.andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withExecutionTimeoutEnabled(true)
						.withExecutionTimeoutInMilliseconds(timeoutInMilliseconds)// ��ʱʱ��
						.withCircuitBreakerErrorThresholdPercentage(60)// �۶����رյ�����ֵ
						.withCircuitBreakerSleepWindowInMilliseconds(3000)));//// �۶����򿪵��رյ�ʱ�䴰����
	}

	/**
	 * �����ź����������Ƿ��۶�
	 *
	 * @param Strategy
	 * @param maxConcurrentRequests
	 * @param timeoutInMilliseconds
	 */
	public SayHelloCommand(HystrixCommandProperties.ExecutionIsolationStrategy Strategy, Integer maxConcurrentRequests,
			Integer timeoutInMilliseconds) {
		super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(SayHelloCommand.class.getName()))
				.andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey(SayHelloCommand.class.getName()))
				.andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter())// �����̳߳�����
				.andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withExecutionIsolationStrategy(Strategy)// �Զ������
						.withExecutionIsolationSemaphoreMaxConcurrentRequests(maxConcurrentRequests) // �ź����Զ�����󲢷���
						.withExecutionTimeoutEnabled(true).withExecutionTimeoutInMilliseconds(timeoutInMilliseconds)// ��ʱʱ��
						.withCircuitBreakerErrorThresholdPercentage(60)// �۶����رյ�����ֵ
						.withCircuitBreakerSleepWindowInMilliseconds(3000)));//// �۶����򿪵��رյ�ʱ�䴰����
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
		Thread.sleep(1001L); // ��ʾ��ʱ����Fallback
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