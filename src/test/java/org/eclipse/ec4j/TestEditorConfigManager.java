package org.eclipse.ec4j;

public class TestEditorConfigManager extends AbstractEditorConfigManager<TestResource>{

	public TestEditorConfigManager() {
		super(new TestResourceProvider());
	}

}
