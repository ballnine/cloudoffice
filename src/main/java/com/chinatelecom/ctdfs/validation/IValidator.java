package com.chinatelecom.ctdfs.validation;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IValidator {
	Boolean validate(HttpServletRequest request,HttpServletResponse response);
}
