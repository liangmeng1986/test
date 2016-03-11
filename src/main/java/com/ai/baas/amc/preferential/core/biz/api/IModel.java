package com.ai.baas.amc.preferential.core.biz.api;

import java.util.Map;

import com.ai.baas.amc.preferential.core.util.BmcException;

public interface IModel {

	void calculate(Map<String, String> businessData) throws BmcException;
	
}
