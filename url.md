## 通用

权限错误返回
```
{
    "retCode": "21",
    "retMsg": "没有访问权限"
}
```

用户id错误
```
{
    "retCode": "11",
    "retMsg": "获取用户信息失败"
}
```

文件id错误
```
{
    "retCode": "12",
    "retMsg": "获取文件ID失败"
}
```


## 个人网盘部分

1、上传文件

url:"/addPersonalFile"

**入参**：multipart/form-data
```
{
  "empeeAcct":int   //员工id
  "parentId":string   //父文件id
  "file":    //上传文件
  "fileName":    //文件名
}
```

**出参**：

正常：
```
{
  "content":"{
    \"retCode\":\"1\",
    \"retMsg\":\"成功！\"
  }"
}
```

目标文件夹已有同名文件：
```
{
  "content":"{
     \"retCode\":\"20\",
     \"retMsg\":\"目标文件重名\"
  }"
}
```

无上传文件：
```
{
  "content":"{
     \"retCode\":\"17\",
     \"retMsg\":\"上传文件格式错误\"
  }"
}
```

**测试数据**:
```
{
  "empeeAcct":8556016,
  "parentId":"8556016",
  "file":...,
  "fileName":"2019-7-14.docx"
}
```

**异常**：
```
{
  "content":"{
    \"retCode\":\"0\",
    \"retMsg\":\"失败！nested exception is org.apache.ibatis.type.TypeException: Could not set parameters for mapping: ParameterMapping{property='ID', mode=IN, javaType=class java.lang.Object, jdbcType=null, numericScale=null, resultMapId='null', jdbcTypeName='null', expression='null'}. Cause: org.apache.ibatis.type.TypeException: Error setting null for parameter #1 with JdbcType OTHER . Try setting a different JdbcType for this parameter or a different jdbcTypeForNull configuration property. Cause: java.sql.SQLException: 无效的列类型: 1111\"
  }"
}
```

2、显示文件列表

url:"/listPersonalFile"

入参：application/x-www-form-urlencoded
```
{
  "empeeAcct":int   //员工id
  "parentId":string   //父文件id
}
```

出参

正常：
```
{
  "content":"{
    \"fileInfo\":[
      {\"FILE_SIZE\":\"1625047\",\"FILE_PARENT\":\"8556010\",\"FILE_SYS\":\"/home/disk/public/files/personal/8556010/2019-7-14.docx\",\"FILE_ID\":\"4ADC9FDF8B0646B292CC409C82637C53\",
      \"FILE_PATH\":\"~\",\"FILE_NAME\":\"2019-7-14.docx\",\"FILE_OWNER\":8556010,\"FILE_CREATION_DATE\":\"2020-03-31 22:39:30\",
      \"FILE_DFSNAME\":\"temp\",\"FILE_STATUS\":1,
      \"FILE_CLASS\":1,
      \"FILE_TYPE\":\"docx\",\"FILE_MODIFICATION_DATE\":\"2020-03-31 22:39:30\"},
      {\"FILE_SIZE\":\"188885\",\"FILE_PARENT\":\"8556010\",\"FILE_SYS\":\"/home/disk/public/files/personal/8556010/饶诗语个人简介.pdf\",\"FILE_ID\":\"36880837E02D41618D4A6DC8D232566C\",
      \"FILE_PATH\":\"~\",
      \"FILE_NAME\":\"饶诗语个人简介.pdf\",\"FILE_OWNER\":8556010,\"FILE_CREATION_DATE\":\"2020-03-31 19:10:28\",
      \"FILE_DFSNAME\":\"temp\",\"FILE_STATUS\":1,
      \"FILE_CLASS\":1,
      \"FILE_TYPE\":\"pdf\",\"FILE_MODIFICATION_DATE\":\"2020-03-31 19:10:28\"}],
      \"retCode\":\"1\",
      \"retMsg\":\"成功！\"
  }"
}
```

**测试数据**：
```
{
  "id":8556016,
  "parentId":"8556016"
}
```

3、下载指定文件

url:"/downloadPersonalFile"

**入参**：application/x-www-form-urlencoded
```
{
  "id":int   //员工id
  "fileId":string   //文件id
  "path":string    //下载本地路径
}
```

**出参**

正常：
```
{
  "content":"{
    \"retCode\":\"1\",
    \"retMsg\":\"成功！\"
  }"
}
```

**测试数据**
```
{
  "id":8556016,
  "fileId":"5ABFF5F0E0944835ADC3F1DAEC9AA394",
  "path":"C:\百度网盘"
}
```


4、查看指定文件具体信息

url:"/listPersonalFileInfo"

**入参**：application/x-www-form-urlencoded
```
{
  "id":int   //员工id
  "fileId":string   //文件id
}
```

**出参**

正常：
```
{
  "content":"{
    \"fileInfo\":
    {\"FILE_SIZE\":\"1625047\",\"FILE_PARENT\":\"8556016\",\"FILE_SYS\":\"/home/disk/public/files/personal/8556016/2019-7-14.docx\",\"FILE_ID\":\"4ADC9FDF8B0646B292CC409C82637C53\",
    \"FILE_PATH\":\"~\",\"FILE_NAME\":\"2019-7-14.docx\",\"FILE_OWNER\":8556016,\"FILE_CREATION_DATE\":\"2020-03-31 22:39:30\",
    \"FILE_DFSNAME\":\"temp\",
    \"FILE_STATUS\":1,
    \"FILE_CLASS\":1,
    \"FILE_TYPE\":\"docx\",\"FILE_MODIFICATION_DATE\":\"2020-03-31 22:39:30\"},
    \"retCode\":\"1\",
    \"retMsg\":\"成功！\"
  }"
}
```

**测试数据**
```
{
  "id":8556016,
  "fileId":"5ABFF5F0E0944835ADC3F1DAEC9AA394"
}
```

5、新建文件夹

url:"/addPersonalFolder"

**入参**：application/x-www-form-urlencoded
```
{
  "id":int   //员工id
  "parentId":string   //父文件id
  "fileName":string   //新建文件夹名
}
```

**出参**

正常：
```
{
  "content": "{
    \"retCode\":\"1\",
    \"retMsg\":\"成功！\"
  }",
  "contentIsJson": true,
  "reqeustSerial": 0,
  "result": 0
}
```

目标文件夹已有同名文件：
```
{
  "content":"{
     \"retCode\":\"20\",
     \"retMsg\":\"目标文件重名\"
  }"
}
```

**测试数据**
```
{
  "id":8556016,
  "parentId":"8556016",
  "fileName":"新建文件夹"
}
```

6、文件重命名

url:"/renamePersonalFile"

**入参**：application/x-www-form-urlencoded
```
{
  "id":int   //员工id
  "fileId":string   //文件id
  "fileName":string   //文件重命名
}
```

**出参**

正常：
```
{
  "content": "{
    \"retCode\":\"1\",
    \"retMsg\":\"成功！\"
  }",
  "contentIsJson": true,
  "reqeustSerial": 0,
  "result": 0
}
```

目标文件夹已有同名文件：
```
{
  "content":"{
     \"retCode\":\"20\",
     \"retMsg\":\"目标文件重名\"
  }"
}
```

**测试数据**
```
{
  "id":8556016,
  "fileId":"6D73EFB450BB4F3E9B2C50D3D330F007",
  "fileName":"rename.pdf"
}
```