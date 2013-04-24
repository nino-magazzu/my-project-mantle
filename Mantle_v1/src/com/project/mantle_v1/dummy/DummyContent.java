package com.project.mantle_v1.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.project.mantle_v1.MantleFile;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContent {

	/**
	 * An array of sample (dummy) items.
	 */
	///public static List<DummyItem> ITEMS = new ArrayList<DummyItem>();

	/**
	 * A map of sample (dummy) items, by ID.
	 */
	public static Map<String, MantleFile> ITEM_MAP = new HashMap<String, MantleFile>();

	

	public static void addItem(MantleFile item) {
		//ITEMS.add(item);
		ITEM_MAP.put(item.getIdFile(), item);
	}

	/**
	 * A dummy item representing a piece of content.
	 */
	
	
}
