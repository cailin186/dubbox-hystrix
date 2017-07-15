package com.alibaba.dubbo.circuitbreak.support.hystrix;

import org.apache.log4j.Logger;

import com.alibaba.dubbo.circuitbreak.util.ProfileUtil;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.rpc.Result;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixThreadPoolKey;

public abstract class HystrixConfig extends HystrixCommand<Result> {

	private static final int DEFAULT_THREADPOOL_CORE_SIZE = 10;
	private static final boolean BREAK_ENABLED = false;// 是否开启断路器
	private static final int ROLLING_STATISTICAL_WINDOW_INMILLISECONDS = 10000;// 统计时间滚动窗口，以毫秒为单位，默认：10秒
	private static final int THRESHOLD_VALUE = 10;// 断路器在整个统计时间内是否开启的阀值，也就是10秒钟内至少请求10次，断路器才发挥起作用
	private static final int BREAKER_SLEEP_MILLISECONDS = 5000;// 断路器默认工作时间，默认:5秒，断路器中断请求5秒后会进入半打开状态，放部分流量过去重试
	private static final int BREAKER_ERROR_PERCENT = 50;// 出错率超过50%启动熔断器

	private static Logger logger = Logger.getLogger(HystrixConfig.class);

	public HystrixConfig(HystrixCommandGroupKey group) {
		super(group);
	}

	public HystrixConfig(Setter setter) {
		super(setter);
	}

	public HystrixConfig(HystrixCommandGroupKey group, HystrixThreadPoolKey threadPool) {
		super(group, threadPool);
	}

	public HystrixConfig(HystrixCommandGroupKey group, int executionIsolationThreadTimeoutInMilliseconds) {
		super(group, executionIsolationThreadTimeoutInMilliseconds);
	}

	public HystrixConfig(HystrixCommandGroupKey group, HystrixThreadPoolKey threadPool,
			int executionIsolationThreadTimeoutInMilliseconds) {
		super(group, threadPool, executionIsolationThreadTimeoutInMilliseconds);
	}

	/**
	 * 线程池配置，可考虑获取Dubbo提供者的线程数来配置断路器的线程数，若使用Hystrix的线程数则应大于Dubbo服务提供者的线程数，保证管道匹配
	 * 
	 * @param url
	 * @return
	 */
	protected static int getThreadPoolCoreSize(URL url) {
		if (url != null) {
			int size = url.getParameter("threadPoolCoreSize", DEFAULT_THREADPOOL_CORE_SIZE);
			if (logger.isDebugEnabled()) {
				logger.debug("ThreadPoolCoreSize:" + size);
			}
			return size;
		}

		return DEFAULT_THREADPOOL_CORE_SIZE;

	}

	/**
	 * 是否启动断路器，默认为启用，可以通过breakerEnabled=false进行关闭
	 * 
	 * @param url
	 * @return
	 */
	protected static boolean getBreakerEnabled(URL url) {
		if (url != null) {
			boolean breakerEnabled = url.getParameter("breakerEnabled", false);
			if (logger.isDebugEnabled()) {
				logger.debug("breakerEnabled:" + breakerEnabled);
			}
			return breakerEnabled;
		}

		return BREAK_ENABLED;

	}

	/**
	 * 统计时间滚动窗口，以毫秒为单位，默认：10秒
	 * 
	 * @param url
	 * @return
	 */
	protected static int getStatisticalWindowSeconds(URL url) {
		if (url != null) {
			int RollingStatisticalWindowInMilliseconds = url.getParameter("RollingStatisticalWindowInMilliseconds",
					ROLLING_STATISTICAL_WINDOW_INMILLISECONDS);
			if (logger.isDebugEnabled()) {
				logger.debug("ROLLING_STATISTICAL_WINDOW_INMILLISECONDS:" + RollingStatisticalWindowInMilliseconds);
			}
			return RollingStatisticalWindowInMilliseconds;
		}

		return ROLLING_STATISTICAL_WINDOW_INMILLISECONDS;

	}

	/**
	 * 断路器在整个统计时间内是否开启的阀值，也就挥起作用是10秒钟内至少请求10次，断路器才发
	 * 
	 * @param url
	 *            dubbo parameters 中包含ThresholdValue时
	 * @return
	 */
	protected static int getThresholdValue(URL url) {
		if (url != null) {
			int ThresholdValue = url.getParameter("ThresholdValue", THRESHOLD_VALUE);
			if (logger.isDebugEnabled()) {
				logger.debug("ThresholdValue:" + ThresholdValue);
			}
			return ThresholdValue;
		}
		return THRESHOLD_VALUE;
	}

	/**
	 * 出错率超过50%启动熔断器
	 * 
	 * @param url
	 *            invoker.url中包含breakerErrorPercent
	 * @return 如果没有设置breakerErrorPercent读取默认值
	 * @author cailin
	 */
	protected static int getBreakerErrorPercent(URL url) {
		if (url != null) {
			int size = url.getParameter("breakerErrorPercent", BREAKER_ERROR_PERCENT);
			if (logger.isDebugEnabled()) {
				logger.debug("breakerErrorPercent:" + BREAKER_ERROR_PERCENT);
			}
			return size;
		}

		return BREAKER_ERROR_PERCENT;
	}

	/**
	 * 断路器默认工作时间，默认:5秒，断路器中断请求5秒后会进入半打开状态，放部分流量过去重试
	 * 
	 * @param url
	 *            读取url中的breakerSleepMilliseconds参数
	 * @return consumer端配置的breakerSleepMilliseconds值，若没有配置返回默认值
	 * @author cailin
	 */
	protected static int getBreakerSleepMilliseconds(URL url) {
		if (url != null) {
			int size = url.getParameter("breakerSleepMilliseconds", BREAKER_SLEEP_MILLISECONDS);
			if (logger.isDebugEnabled()) {
				logger.debug("breakerSleepMilliseconds:" + size);
			}
			return size;
		}

		return BREAKER_SLEEP_MILLISECONDS;

	}

}