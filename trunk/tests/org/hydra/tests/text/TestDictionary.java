package org.hydra.tests.text;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.tests.utils.Utils4Tests;
import org.hydra.text.TextManager;
import org.hydra.utils.Constants;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.BeanFactory;

public class TestDictionary {
	Log _log = LogFactory.getLog(this.getClass());
	BeanFactory factory = Utils4Tests.getBeanFactory();

	@Test
	public void test_main() {
		Assert.assertNotNull(factory);
		Assert.assertTrue(factory.containsBean(Constants._beans_text_manager));
		Assert.assertTrue(factory.getBean(Constants._beans_text_manager) instanceof TextManager);
		// Test defaults: basename & locale
		TextManager textMananger = (TextManager)factory.getBean(Constants._beans_text_manager);
		Assert.assertNotNull(textMananger.getBasename());
	}
	
}
