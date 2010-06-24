package org.hydra.tests.bean;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

public class TestCrossRefBeans {
	
	public final static Resource res = new FileSystemResource("WebContext/WEB-INF/test.xml");	
	public static XmlBeanFactory factory = null;
	
	@Before
	public void first(){
		factory = new XmlBeanFactory(res);
	}
	
	@Test
	public void test_0_beans_existance(){
		Assert.assertTrue(factory.containsBean("Test1"));
		Assert.assertTrue(factory.containsBean("Test1.1"));
		Assert.assertTrue(factory.containsBean("Test1.2"));
	}
	
	@Test
	public void test_1_beans_links(){
		Assert.assertTrue(factory.getBean("Test1") instanceof Bean4TestCross);
		Bean4TestCross bean = (Bean4TestCross) factory.getBean("Test1");
		
		Assert.assertTrue(bean.containsLink("Test1.1"));
		Assert.assertTrue(bean.containsLink("Test1.2"));
	}	
	
	
	@Test
	public void test_2_beans_cross_links(){
		Assert.assertTrue(factory.getBean("Test1") instanceof Bean4TestCross);
		Bean4TestCross bean1 = (Bean4TestCross) factory.getBean("Test1");
		Bean4TestCross bean12 = (Bean4TestCross) factory.getBean("Test1.2");
		
		Assert.assertTrue(bean1.containsLink("Test1.2"));
		Assert.assertTrue(bean12.containsLink("Test1"));
		
		Assert.assertEquals(bean12, bean1.getLink("Test1.2").getObject());
	}		
}
