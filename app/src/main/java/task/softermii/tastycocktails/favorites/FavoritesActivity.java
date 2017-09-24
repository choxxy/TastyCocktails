/*
 * Copyright 2017 Dmitriy Ponomarenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package task.softermii.tastycocktails.favorites;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;

import task.softermii.tastycocktails.BaseActivity;
import task.softermii.tastycocktails.R;
import task.softermii.tastycocktails.cocktails.SearchFragment;

/**
 * Created on 24.09.2017.
 * @author Dimowner
 */
public class FavoritesActivity extends BaseActivity {

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.base_nav_activity);

		if (savedInstanceState == null) {
			FragmentManager manager = getSupportFragmentManager();
			SearchFragment fragment = SearchFragment.newInstance(SearchFragment.TYPE_FAVORITES);
			manager
					.beginTransaction()
					.add(R.id.fragment, fragment, "cocktails_fragment")
					.commit();
		}
	}

	@Override
	protected int getSelfNavDrawerItem() {
		return NAVDRAWER_ITEM_FAVORITES;
	}
}
