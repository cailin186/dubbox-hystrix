package com.alibaba.dubbo.circuitbreak.fallback;
/**
 * @author caohui01
 */
public class FallbackMeta {

	private String transportBeanName;
	private String initValue;

	public String getTransportBeanName() {
		return transportBeanName;
	}

	public void setTransportBeanName(String transportBeanName) {
		this.transportBeanName = transportBeanName;
	}

	public String getInitValue() {
		return initValue;
	}

	public void setInitValue(String initValue) {
		this.initValue = initValue;
	}
}
