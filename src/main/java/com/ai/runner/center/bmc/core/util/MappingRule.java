package com.ai.runner.center.bmc.core.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.runner.center.bmc.core.persistence.entity.BmcRecordFmt;
import com.ai.runner.center.bmc.core.persistence.entity.BmcRecordFmt.BmcRecordFmtKey;

public class MappingRule {
	private static Logger logger = LoggerFactory.getLogger(MappingRule.class);
	// public static final int FORMAT_TYPE_PRE = 1;
	// public static final int FORMAT_TYPE_MATCH = 2;
	// public static final int FORMAT_TYPE_CAL = 3;
	public static final int FORMAT_TYPE_INPUT = 1;
	public static final int FORMAT_TYPE_OUTPUT = 2;
	private Map<BmcRecordFmtKey, Map<String, Integer>> recordFmtMap = new HashMap<BmcRecordFmtKey, Map<String, Integer>>();

	/**
	 * 获取当前类型的映射规则
	 * 
	 * @param formatType
	 * @return
	 */
	public static MappingRule getMappingRule(int formatType) {
		logger.debug("load mapping " + formatType);
		MappingRule mappingRule = new MappingRule();
		//JdbcTemplate jdbcTemplate = (JdbcTemplate) ApplicationContextUtil.getBean("mysqlJdbcTemplate");
		//String recordFmtSql = "select * from bmc_record_fmt where FORMAT_TYPE=" + formatType;
		StringBuilder recordFmtSql = new StringBuilder();
		recordFmtSql.append("select r.system_id systemId,r.tenant_id tenantId,r.service_id serviceId,");
		recordFmtSql.append("       r.format_type formatType,r.field_serial fieldSerial,r.field_name fieldName,");
		recordFmtSql.append("       r.field_code fieldCode ");
		recordFmtSql.append("from bmc_record_fmt r ");
		recordFmtSql.append("where r.FORMAT_TYPE=").append(formatType);
		//List<BmcRecordFmt> recordFmtList = jdbcTemplate.query(recordFmtSql, new BeanPropertyRowMapper<BmcRecordFmt>(BmcRecordFmt.class));
		List<BmcRecordFmt> recordFmtList = JdbcTemplate.query(recordFmtSql.toString(), new BeanListHandler<BmcRecordFmt>(BmcRecordFmt.class));
		mappingRule.init(recordFmtList);
		return mappingRule;
	}

	/**
	 * 获取当前类型之前的映射规则
	 * 
	 * @param formatType
	 * @return
	 */
	public static MappingRule getMappingRuleBefore(int formatType) {
		logger.debug("load mapping before " + formatType);
		MappingRule mappingRule = new MappingRule();
		//JdbcTemplate jdbcTemplate = (JdbcTemplate) ApplicationContextUtil.getBean("mysqlJdbcTemplate");
		//String recordFmtSql = "select * from bmc_record_fmt where FORMAT_TYPE<=" + formatType;
		StringBuilder recordFmtSql = new StringBuilder();
		recordFmtSql.append("select r.system_id systemId,r.tenant_id tenantId,r.service_id serviceId,");
		recordFmtSql.append("       r.format_type formatType,r.field_serial fieldSerial,r.field_name fieldName,");
		recordFmtSql.append("       r.field_code fieldCode ");
		recordFmtSql.append("from bmc_record_fmt r ");
		recordFmtSql.append("where FORMAT_TYPE<=").append(formatType);
		List<BmcRecordFmt> recordFmtList = JdbcTemplate.query(recordFmtSql.toString(), new BeanListHandler<BmcRecordFmt>(BmcRecordFmt.class));
		mappingRule.init(recordFmtList);
		return mappingRule;
	}

	public void init(List<BmcRecordFmt> recordFmtList) {
		for (BmcRecordFmt bmcRecordFmt : recordFmtList) {
			Map<String, Integer> indexMap = recordFmtMap.get(bmcRecordFmt.getBmcRecordFmtKey());
			if (indexMap == null) {
				indexMap = new HashMap<>();
				recordFmtMap.put(bmcRecordFmt.getBmcRecordFmtKey(), indexMap);
			}
			indexMap.put(bmcRecordFmt.getFieldCode(), bmcRecordFmt.getFieldSerial());
		}
		for (Map<String, Integer> map : recordFmtMap.values()) {
			map = sortMap(map);
		}
		System.out.println("there is " + recordFmtMap.size() + " mapping loaded!");
		logger.debug("there is " + recordFmtMap.size() + " mapping loaded!");
	}

	private static Map<String, Integer> sortMap(Map<String, Integer> oldMap) {
		ArrayList<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(oldMap.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {

			@Override
			public int compare(Entry<java.lang.String, Integer> arg0, Entry<java.lang.String, Integer> arg1) {
				return arg0.getValue() - arg1.getValue();
			}
		});
		Map<String, Integer> newMap = new LinkedHashMap<String, Integer>();
		for (int i = 0; i < list.size(); i++) {
			newMap.put(list.get(i).getKey(), list.get(i).getValue());
		}
		return newMap;
	}

	public Map<String, Integer> getIndexes(BmcRecordFmtKey bmcRecordFmtKey) {
		return recordFmtMap.get(bmcRecordFmtKey);
	}
}
