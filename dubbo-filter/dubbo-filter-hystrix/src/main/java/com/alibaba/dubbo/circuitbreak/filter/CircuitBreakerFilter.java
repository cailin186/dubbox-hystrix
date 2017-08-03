package com.alibaba.dubbo.circuitbreak.filter;

import com.alibaba.dubbo.circuitbreak.CircuitBreaker;
import com.alibaba.dubbo.circuitbreak.CircuitBreakerFactory;
import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.common.utils.ConfigUtils;
import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcException;

/**
 * @author Jerry nickid@qq.com
 * @create 2017-04-25 13:45
 **/
@Activate(group = Constants.CONSUMER, value = Constants.CIRCUIT_BREAKER_KEY)
public class CircuitBreakerFilter implements Filter {
    private CircuitBreakerFactory circuitBreakerFactory;

    public void setCircuitBreakerFactory(CircuitBreakerFactory circuitBreakerFactory) {
        this.circuitBreakerFactory = circuitBreakerFactory;
    }

    /**
     * @param invoker    service
     * @param invocation invocation.
     * @return com.alibaba.dubbo.rpc.Result
     * @throws RpcException
     */
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
    	// URL当中的circuitbreak参数为非空，则经过hystrix执行业务逻辑
        if (circuitBreakerFactory != null && ConfigUtils.isNotEmpty(invoker.getUrl().getMethodParameter(invocation.getMethodName(), Constants.CIRCUIT_BREAKER_KEY))) {
        	// 根据URL当中的参数设置初始化hystrix参数并且实例化熔断器实例
            CircuitBreaker circuitBreaker = circuitBreakerFactory.getCircuitBreaker(invoker, invocation);
            if (circuitBreaker != null) {
                return circuitBreaker.circuitBreak();
            }
        }
        return invoker.invoke(invocation);
    }
}
