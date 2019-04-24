package com.zcc.codegenerator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.FileOutConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.TemplateConfig;
import com.baomidou.mybatisplus.generator.config.converts.PostgreSqlTypeConvert;
import com.baomidou.mybatisplus.generator.config.po.TableFill;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;
import com.zcc.codegenerator.config.DBConfig;

public class ZccCodeGenerator {
	public static void generator(String module,String[] tables,String author,DBConfig dbconfig) {
				// 代码生成器
				AutoGenerator mpg = new AutoGenerator();

				// 全局配置
				String projectPath = System.getProperty("user.dir");
				String mavenSrcJavaPath="/src/main/java";
				String mavenSrcTestPath="/src/test/java";
				GlobalConfig gc = new GlobalConfig()		
							.setOutputDir(projectPath + mavenSrcJavaPath)
							.setAuthor(author==null?"gdctsy":author)
							.setOpen(false)
							.setBaseColumnList(true)
							.setFileOverride(true)  // 文件覆盖
							.setIdType(IdType.ID_WORKER_STR)//主键id策略
							.setXmlName("%sDao")
							.setServiceName("%sService")
							.setServiceImplName("%sServiceImpl")
							.setMapperName("%sDao");
				
				mpg.setGlobalConfig(gc);

				// 数据源配置
				DataSourceConfig dsc = new DataSourceConfig()
								.setTypeConvert(new PostgreSqlTypeConvert() {
		                            // 自定义数据库表字段类型转换【可选】
		                            @Override
		                            public DbColumnType processTypeConvert(GlobalConfig globalConfig,String fieldType) {		                                
		                                 if ( fieldType.toLowerCase().contains( "numeric" ) ) {
		                                	 return DbColumnType.DOUBLE;
		                                 }
		                                return (DbColumnType)super.processTypeConvert(globalConfig,fieldType);
		                            }
		                        })
								.setUrl(dbconfig.getUrl())
								.setDriverName(dbconfig.getDriverClassName())
								.setUsername(dbconfig.getUsername())
								.setPassword(dbconfig.getPassword());
								if(dbconfig.getUrl().contains("jdbc:postgresql")) {
									dsc.setDbType(DbType.POSTGRE_SQL);
								}else if(dbconfig.getUrl().contains("jdbc:mysql")){
									dsc.setDbType(DbType.MYSQL);
								}else if(dbconfig.getUrl().contains("jdbc:oracle")){
									dsc.setDbType(DbType.ORACLE);
								}else if(dbconfig.getUrl().contains("jdbc:sqlserver")){
									dsc.setDbType(DbType.SQL_SERVER);					
								}
				mpg.setDataSource(dsc);
				// 包配置
				PackageConfig pc = new PackageConfig()
				              //.setModuleName(scanner("模块名"))
							  .setModuleName(module)
				              .setParent("com.zcc.microservice")
				              .setEntity("model")
				              .setController("controller.api")
				              .setXml("dao.mapper")
				              .setMapper("dao");
				mpg.setPackageInfo(pc);

				// 自定义配置
				InjectionConfig cfg = new InjectionConfig() {
					@Override
					public void initMap() {
						// to do nothing
					}
				};
				//mapper模板
				String templatePath = "/templates/generator/mapper.xml.vm";
				// 自定义输出配置
				List<FileOutConfig> focList = new ArrayList<>();
				// 自定义配置会被优先输出
//				focList.add(new FileOutConfig(templatePath) {
//					@Override
//					public String outputFile(TableInfo tableInfo) {
//						// 自定义输出文件名 ， 如果你 Entity 设置了前后缀、此处注意 xml 的名称会跟着发生变化！！
//						return projectPath + mavenSrcResourcePath+"/mapper/" + pc.getModuleName() + "/"
//								+ tableInfo.getEntityName() + "Dao" + StringPool.DOT_XML;
//					}
//				});
				
				// 自定义测试类配置会被优先输出
				String testClassTemplatePath = "/templates/generator/controller.test.java.vm";
				String packageClassPath = pc.getParent().replaceAll("\\.", "\\/");
				String controllerPackageClassPath = packageClassPath+"/"+pc.getController().replaceAll("\\.", "\\/");
				focList.add(new FileOutConfig(testClassTemplatePath) {
					@Override
					public String outputFile(TableInfo tableInfo) {					
						return projectPath + mavenSrcTestPath+File.separator + controllerPackageClassPath+File.separator
								+ tableInfo.getEntityName() + "ControllerTests" + StringPool.DOT_JAVA ;
					}
				});
				
				// 自定义微服务启动类配置会被优先输出
//				String applicationClassTemplatePath = "/templates/generator/application.java.vm";
//				focList.add(new FileOutConfig(applicationClassTemplatePath) {
//					@Override
//					public String outputFile(TableInfo tableInfo) {
//						return projectPath + mavenSrcJavaPath+File.separator + packageClassPath+File.separator
//								+tableInfo.getEntityName() + "Application" + StringPool.DOT_JAVA ;
//					}
//				});


				cfg.setFileOutConfigList(focList);
				mpg.setCfg(cfg);

				// 配置模板
				TemplateConfig templateConfig = new TemplateConfig();
				// 指定自定义模板路径，注意不要带上.ftl/.vm, 会根据使用的模板引擎自动识别
				templateConfig.setEntity("templates/generator/entity.java");
				// templateConfig.setService();
				templateConfig.setController("templates/generator/controller.java");
				templateConfig.setXml("templates/generator/mapper.xml");
				mpg.setTemplate(templateConfig);
				

				// 自定义需要填充的字段
				//如 每张表都有一个创建时间、修改时间
		        //而且这基本上就是通用的了，新增时，创建时间和修改时间同时修改
		        //修改时，修改时间会修改，
		        //虽然像Mysql数据库有自动更新几只，但像ORACLE的数据库就没有了，
		        //使用公共字段填充功能，就可以实现，自动按场景更新了。
		        //如下是配置
		        List<TableFill> tableFillList = new ArrayList<TableFill>();		        
		        TableFill createField = new TableFill("create_time", FieldFill.INSERT); 
		        TableFill modifiedField = new TableFill("modified_time", FieldFill.INSERT_UPDATE); 
		        tableFillList.add(createField);
		        tableFillList.add(modifiedField);
		        
				// 策略配置
				String tablePrefix = "t_";
				if(StringUtils.isNotEmpty(dbconfig.getTablePrefix())) {
					tablePrefix = tablePrefix + ","+dbconfig.getTablePrefix();
				}
				
				StrategyConfig strategy = new StrategyConfig()
								.setTablePrefix(tablePrefix.split(","))
								.setNaming(NamingStrategy.underline_to_camel)
								.setColumnNaming(NamingStrategy.underline_to_camel)
								.setTableFillList(tableFillList)
								//.setSuperEntityClass("com.wing.pimsa.core.model.BaseModel")	
								.setEntityLombokModel(false)
								.setRestControllerStyle(true)
								//.setSuperControllerClass("com.baomidou.ant.common.BaseController")
								.setInclude(tables)						
								//.setSuperEntityColumns(new String[]{"id","create_time","modified_time"})
								.setControllerMappingHyphenStyle(true);
								//.setTablePrefix(pc.getModuleName() + "_");
				
				mpg.setStrategy(strategy);
				//mpg.setTemplateEngine(new FreemarkerTemplateEngine());
				mpg.setTemplateEngine(new VelocityTemplateEngine());
				mpg.execute();
				System.out.println("1."+pc.getModuleName()+"模块的代码创建成功，请刷新工程目录");
				System.out.println("2.查看工程目录:"+mavenSrcJavaPath+"下的"+pc.getParent());
	}
	
	
	
	/**
	 * <p>
	 * 读取控制台内容
	 * </p>
	 */
	public static String scanner(String tip) {
		Scanner scanner = new Scanner(System.in);
		StringBuilder help = new StringBuilder();
		help.append("请输入" + tip + "：");
		System.out.println(help.toString());
		if (scanner.hasNext()) {
			String ipt = scanner.next();
			if (StringUtils.isNotEmpty(ipt)) {
				return ipt;
			}
		}
		throw new MybatisPlusException("请输入正确的" + tip + "！");
	}
	
}
