package com.generator;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;

public class Generator {
	
		public static void main(String[] args) throws Exception {
		//MBG执行过程中的警告信息
			List<String> warnings = new ArrayList<String>();
			//当生成的代码重复时，覆盖源代码
			boolean overwrite =false;
			//读取generator配置文件
			InputStream is = Generator.class.getResourceAsStream("/generator/generatorConfig.xml");
			ConfigurationParser cp  = new ConfigurationParser(warnings);
			Configuration config = cp.parseConfiguration(is);
			is.close();
			
			DefaultShellCallback callback = new DefaultShellCallback(overwrite);
			//创建MBG
			MyBatisGenerator generator = new MyBatisGenerator(config, callback, warnings);
			
			//执行代码生成
			generator.generate(null);
			
			//输出警告信息
			for (String string : warnings) {
				System.out.println(string);
			}
	}
	
}