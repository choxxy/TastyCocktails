/*
 * Copyright 2018 Dmitriy Ponomarenko
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

package com.dimowner.tastycocktails.dagger.rating;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import android.support.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.dimowner.tastycocktails.FirebaseHandler;
import com.dimowner.tastycocktails.data.Prefs;
import com.dimowner.tastycocktails.data.Repository;
import com.dimowner.tastycocktails.data.RepositoryContract;
import com.dimowner.tastycocktails.rating.RatingContract;
import com.dimowner.tastycocktails.rating.RatingPresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class RatingModule {

	private Fragment fragment;

	public RatingModule(Fragment fragment) {
		this.fragment = fragment;
	}

	@Provides
	@RatingScope
	RatingContract.UserActionsListener provideRatingPresenter(Repository repository, FirebaseHandler firebaseHandler, Prefs prefs) {
		return ViewModelProviders.of(fragment, new RatingViewModelFactory(repository, firebaseHandler, prefs)).get(RatingPresenter.class);
	}

	public static class RatingViewModelFactory implements ViewModelProvider.Factory {
		private RepositoryContract repository;
		private FirebaseHandler firebaseHandler;
		private Prefs prefs;


		public RatingViewModelFactory(RepositoryContract repository, FirebaseHandler firebaseHandler, Prefs prefs) {
			this.repository = repository;
			this.firebaseHandler = firebaseHandler;
			this.prefs = prefs;
		}

		@NonNull
		@Override
		public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
			return (T) new RatingPresenter(repository, firebaseHandler, prefs);
		}
	}
}
