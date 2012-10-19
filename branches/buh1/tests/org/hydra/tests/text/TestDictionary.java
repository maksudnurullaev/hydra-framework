package org.hydra.tests.text;

import org.hydra.managers.TextManager;
import org.hydra.tests.BaseTest;
import org.hydra.tests.utils.BeansUtils4Tests;
import org.hydra.utils.Constants;
import org.junit.Assert;
import org.junit.Test;

public class TestDictionary extends BaseTest {
	@Test
	public void test_main() {
		Assert.assertTrue(BeansUtils4Tests.getBean(Constants._beans_text_manager) != null);
		Assert.assertTrue(BeansUtils4Tests.getBean(Constants._beans_text_manager) instanceof TextManager);
		// Test defaults: basename & locale
		TextManager textMananger = (TextManager)BeansUtils4Tests.getBean(Constants._beans_text_manager);
		Assert.assertNotNull(textMananger.getBasename());
		Assert.assertNotNull(textMananger.getDefaultLocale());
	}
	
}
