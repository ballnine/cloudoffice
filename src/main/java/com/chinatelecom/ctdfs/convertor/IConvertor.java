package com.chinatelecom.ctdfs.convertor;

import java.util.List;

import com.chinatelecom.ctdfs.handler.IFormFieldInfo;


public interface IConvertor {
	List<IFormFieldInfo> convert(List<IFormFieldInfo> webFields);
}
