package com.chinatelecom.ctdfs.uploader;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.chinatelecom.ctdfs.handler.IFormFieldInfo;

public interface IUploader {

	List<IFormFieldInfo> upload(HttpServletRequest request);
}
