package com.alibaba.dubbo.circuitbreak.fallback;

import java.io.Serializable;

/**
 * @author caohui01
 */
public interface TransportFallback extends Serializable{

	String getFallback();
}
