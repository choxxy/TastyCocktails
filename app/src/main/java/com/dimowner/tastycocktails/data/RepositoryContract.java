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

package com.dimowner.tastycocktails.data;

import android.support.annotation.NonNull;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import com.dimowner.tastycocktails.data.model.Drink;

/**
 * Created on 27.07.2017.
 * @author Dimowner
 */
public interface RepositoryContract {

	Single<List<Drink>> searchCocktailsByName(@NonNull String search);
	Single<List<Drink>> searchCocktailsByIngredient(@NonNull String ingredient);
	Single<Drink> getRandomCocktail();
	Single<Drink> getCocktail(long id);
	Flowable<List<Drink>> getLastSearch(String query);
	Flowable<List<Drink>> getFavorites();
	Completable addToFavorites(Drink drink);
	Completable removeFromFavorites(long id);
	Completable reverseFavorite(long id);
}