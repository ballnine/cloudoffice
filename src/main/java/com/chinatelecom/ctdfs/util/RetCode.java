package com.chinatelecom.ctdfs.util;

public class RetCode {
	/**
	 * 通用返回标识
	 */
	public static final String RESULT_KEY = "retCode";
	public static final String RESULT_VALUE = "retMsg";
	public static final String RESULT_DATA = "retData";

	/** 业务成功 */
	public static final String SUCCESS = "1";
	public static final String SUCCESS_MSG = "成功！";

	/** 业务失败 */
	public static final String FAIL = "0";
	public static final String FAIL_MSG = "失败！";

	public static final String NODATA = "2";
	public static final String NODATA_MSG = "未查询到数据";

	/** 未知错误 */
	public static final String UNKOWN = "-1";
	public static final String UNKOWN_MSG = "未知错误";
	
	/** imtoken失败 */
	public static final String IM_ERROR = "414";
	public static final String IM_ERROR_MSG = "获取imtoken失败";
	
	public static final String ERROR_CODE_11 = "11";
	public static final String ERROR_DESC_11 = "获取用户信息失败";
	
	public static final String ERROR_CODE_12 = "12";
	public static final String ERROR_DESC_12 = "获取文件ID失败";
	
	public static final String ERROR_CODE_13 = "13";
	public static final String ERROR_DESC_13 = "更新个人文件状态失败";
	
	public static final String ERROR_CODE_14 = "14";
	public static final String ERROR_DESC_14 = "获取审核文件状态失败";
	
	public static final String ERROR_CODE_15 = "15";
	public static final String ERROR_DESC_15 = "获取上传文件列表失败";
	
	public static final String ERROR_CODE_16 = "16";
	public static final String ERROR_DESC_16 = "获取文件详细信息失败";
	
	public static final String ERROR_CODE_17 = "17";
	public static final String ERROR_DESC_17 = "上传文件格式错误";
	
	public static final String ERROR_CODE_18 = "18";
	public static final String ERROR_DESC_18 = "下载目标文件不存在";
	
	public static final String ERROR_CODE_19 = "19";
	public static final String ERROR_DESC_19 = "获取目标文件失败";
	
	public static final String ERROR_CODE_20 = "20";
	public static final String ERROR_DESC_20 = "目标文件重名";
	
	public static final String ERROR_CODE_21 = "21";
	public static final String ERROR_DESC_21 = "没有访问权限";
	
	public static final String ERROR_CODE_22 = "22";
	public static final String ERROR_DESC_22 = "没有提供文件种类";
}
