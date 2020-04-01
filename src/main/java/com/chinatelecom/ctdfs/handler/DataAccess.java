package com.chinatelecom.ctdfs.handler;

import com.chinatelecom.ctdfs.convertor.IConvertor;
import com.chinatelecom.ctdfs.uploader.IUploader;
import com.chinatelecom.ctdfs.validation.IValidator;

public class DataAccess {
	private static final String convertSuffix="Convertor";
	private static final String uploadSuffix="uploader";
	private static final String validateSuffix="Validator";
	private static final String validatePackage="validation.";
	private static final String pathPrefix="com.chinatelecom.ctdfs.";
	private DataAccess dataaccess;
	public DataAccess getInstance() {
		if(dataaccess==null) {
			dataaccess=new DataAccess();
		}
		return dataaccess;
	}
	
	public static IConvertor createConvertor(String convertorType) throws InstantiationException, IllegalAccessException {
		String className=convertorType+convertSuffix;
		try {
			Class<?> convertorClass=Class.forName(className);
			return (IConvertor) convertorClass.newInstance();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static IUploader createUploader(String UploaderType) throws InstantiationException, IllegalAccessException {
		String className=UploaderType+uploadSuffix;
		try {
			Class<?> convertorClass=Class.forName(className);
			return (IUploader) convertorClass.newInstance();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static IValidator createValidator(String validatorType) throws InstantiationException, IllegalAccessException {
		String className=pathPrefix+validatePackage+validatorType+validateSuffix;
		try {
			Class<?> validatorClass=Class.forName(className);
			return (IValidator) validatorClass.newInstance();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
