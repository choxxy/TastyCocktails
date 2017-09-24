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

package task.softermii.tastycocktails.data;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import task.softermii.tastycocktails.TCApplication;
import task.softermii.tastycocktails.data.model.Drink;
import task.softermii.tastycocktails.data.room.AppDatabase;
import task.softermii.tastycocktails.data.room.CocktailsDao;
import timber.log.Timber;

/**
 * Created on 27.07.2017.
 * @author Dimowner
 */
public class LocalRepository implements RepositoryContract {

	@Inject
	AppDatabase appDatabase;

	public LocalRepository(Context context) {
		TCApplication.get(context).applicationComponent().inject(this);
	}

	private CocktailsDao getRepositoriesDao() {
		return appDatabase.cocktailsDao();
	}

	@Override
	public Single<List<Drink>> searchCocktailsByName(@NonNull String search) {
		throw new RuntimeException("This method is supported only in RemoteRepository");
	}

	@Override
	public Single<List<Drink>> searchCocktailsByIngredient(@NonNull String ingredient) {
		throw new RuntimeException("This method is supported only in RemoteRepository");
	}

	@Override
	public Single<Drink> getRandomCocktail() {
		return getRepositoriesDao().getLastSearchRowCount().subscribeOn(Schedulers.io()).flatMap(count -> {
			if (count > 0) {
				return getRepositoriesDao()
						.getRandom()
						.subscribeOn(Schedulers.io());
			} else {
				return Single.fromCallable(Drink::emptyDrink);
			}
		});
	}

	@Override
	public Single<Drink> getCocktail(long id) {
		return getRepositoriesDao().getLastSearchRowCount().subscribeOn(Schedulers.io()).flatMap(count -> {
			if (count > 0) {
				return getRepositoriesDao()
						.getCocktail(id)
						.subscribeOn(Schedulers.io());
			} else {
				return Single.fromCallable(Drink::emptyDrink);
			}
		});
	}

	@Override
	public Single<List<Drink>> getLastSearch() {
		return getRepositoriesDao().getLastSearchRowCount().subscribeOn(Schedulers.io()).flatMap(count -> {
			if (count > 0) {
				return getRepositoriesDao().getLastSearch();
			} else {
				return Single.fromCallable(Collections::emptyList);
			}
		});
	}

	@Override
	public Single<List<Drink>> getFavorites() {
		return getRepositoriesDao().getFavoritesRowCount().subscribeOn(Schedulers.io()).flatMap(count -> {
			if (count > 0) {
				return getRepositoriesDao().getFavorites();
			} else {
				return Single.fromCallable(Collections::emptyList);
			}
		});
	}

	@Override
	public Completable addToFavorites(long id) {
		return Completable.fromAction(() -> getRepositoriesDao().addToFavorites(id));
	}

	@Override
	public Completable removeFromFavorites(long id) {
		return Completable.fromAction(() -> getRepositoriesDao().removeFromFavorites(id));
	}

	/**
	 * Rewrite local cached Drinks
	 * @param items new Drinks to save.
	 */
	public void rewriteDrinks(List<Drink> items) {
		Single.just(items).map(data -> {
//				getRepositoriesDao().deleteAll();
				getRepositoriesDao().deleteLastSearch();
				getRepositoriesDao().insertAll(data.toArray(new Drink[data.size()]));
				return null;
			})
			.subscribeOn(Schedulers.io())
			.subscribe((o, throwable) -> Timber.e(throwable));
	}
}
