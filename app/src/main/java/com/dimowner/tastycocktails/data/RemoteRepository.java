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



import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import okhttp3.CipherSuite;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import timber.log.Timber;

import com.dimowner.tastycocktails.AppConstants;
import com.dimowner.tastycocktails.ModelMapper;
import com.dimowner.tastycocktails.data.model.Drink;
import com.dimowner.tastycocktails.data.model.Drinks;
import com.dimowner.tastycocktails.data.model.RatingDrink;

/**
 * Created on 27.07.2017.
 * @author Dimowner
 */
public class RemoteRepository implements RepositoryContract {

	private static final String API_KEY = "1";
	private static final String API_URL = "http://www.thecocktaildb.com/api/json/v1/" + API_KEY + "/";

	private Retrofit retrofit;

	private CocktailApi cocktailApi;


	@Override
	public Flowable<List<Drink>> searchCocktailsByName(@NonNull String search) {
		return getCocktailApi()
					.searchByName(search)
					.map(ModelMapper::convertDrinksToList)
					.subscribeOn(Schedulers.io());

	}

	@Override
	public Flowable<List<Drink>> searchCocktailsByNameLocal(@NonNull String search) {
		throw new UnsupportedOperationException("This method is supported only in LocalRepository");
	}

	@Override
	public Flowable<List<Drink>> getDrinksHistory(int page) {
		throw new UnsupportedOperationException("This method is supported only in LocalRepository");
	}
//
//	@Override
//	public Flowable<List<Drink>> loadDrinksWithFilter(int filterType, String value) {
//		if (filterType == Prefs.FILTER_TYPE_CATEGORY) {
//			return getCocktailApi().searchByCategory(value)
//					.map(ModelMapper::convertDrinksToList)
//					.map(drinks -> {
//						for (int i = 0; i < drinks.size(); i++) {
//							drinks.get(i).setStrCategory(value);
//						}
//						return drinks;
//					});
//		} else if (filterType == Prefs.FILTER_TYPE_INGREDIENT) {
//			return getCocktailApi().searchByIngredient(value)
//					.map(ModelMapper::convertDrinksToList)
//					.map(drinks -> {
//						for (int i = 0; i < drinks.size(); i++) {
//							drinks.get(i).setStrIngredient1(value);
//						}
//						return drinks;
//					});
//		} else if (filterType == Prefs.FILTER_TYPE_GLASS) {
//			return getCocktailApi().searchByGlass(value)
//					.map(ModelMapper::convertDrinksToList)
//					.map(drinks -> {
//						for (int i = 0; i < drinks.size(); i++) {
//							drinks.get(i).setStrGlass(value);
//						}
//						return drinks;
//					});
//		} else if (filterType == Prefs.FILTER_TYPE_ALCOHOLIC_NON_ALCOHOLIC) {
//			return getCocktailApi().searchByAlcoholic(value)
//					.map(ModelMapper::convertDrinksToList)
//					.map(drinks -> {
//						for (int i = 0; i < drinks.size(); i++) {
//							drinks.get(i).setStrAlcoholic(value);
//						}
//						return drinks;
//					});
//		} else {
//			throw new UnsupportedOperationException("This is not implemented yet");
//		}
//	}

	@Override
	public Flowable<List<Drink>> loadFilteredDrinks(String category, String ingredient, String glass, String alcoholic) {
		if (category != null && !category.isEmpty()) {
			return getCocktailApi().searchByCategory(category)
					.map(ModelMapper::convertDrinksToList)
					.map(drinks -> {
						for (int i = 0; i < drinks.size(); i++) {
							drinks.get(i).setStrCategory(category);
						}
						return drinks;
					});
		} else if (ingredient != null && !ingredient.isEmpty()) {
			return getCocktailApi().searchByIngredient(ingredient)
					.map(ModelMapper::convertDrinksToList)
					.map(drinks -> {
						for (int i = 0; i < drinks.size(); i++) {
							drinks.get(i).setStrIngredient1(ingredient);
						}
						return drinks;
					});
		} else if (glass != null && !glass.isEmpty()) {
			return getCocktailApi().searchByGlass(glass)
					.map(ModelMapper::convertDrinksToList)
					.map(drinks -> {
						for (int i = 0; i < drinks.size(); i++) {
							drinks.get(i).setStrGlass(glass);
						}
						return drinks;
					});
		} else if (alcoholic != null && !alcoholic.isEmpty()) {
			return getCocktailApi().searchByAlcoholic(alcoholic)
					.map(ModelMapper::convertDrinksToList)
					.map(drinks -> {
						for (int i = 0; i < drinks.size(); i++) {
							drinks.get(i).setStrAlcoholic(alcoholic);
						}
						return drinks;
					});
		} else {
			throw new UnsupportedOperationException("Error on filter apply!");
		}
	}

	@Override
	public Flowable<List<Drink>> loadFilteredDrinks2(String category, List<String> ingredients, String glass, String alcoholic) {
		throw new UnsupportedOperationException("This method is supported only in LocalRepository");
	}

	@Override
	public Single<Drink> getRandomCocktail() {
		return getCocktailApi()
				.getRandom()
				.map(ModelMapper::convertDrinksToDrink)
				.subscribeOn(Schedulers.io());
	}

	@Override
	public Single<Drink> getRandomCocktail(List<String> ingredients) {
		throw new UnsupportedOperationException("This method is supported only in LocalRepository");
	}

	@Override
	public Flowable<Drink> getCocktailRx(long id) {
		return getCocktailApi()
				.getCocktail(id)
				.map(ModelMapper::convertDrinksToDrink)
				.subscribeOn(Schedulers.io());
	}

	@Override
	public Single<Drink> getLocalCocktailRx(long id) {
		throw new UnsupportedOperationException("This method is supported only in LocalRepository");
	}

	@Override
	public Flowable<List<Drink>> getLastSearch(String query) {
		throw new UnsupportedOperationException("This method is supported only in LocalRepository");
	}

	@Override
	public Flowable<List<Drink>> getFavorites() {
		throw new UnsupportedOperationException("This method is supported only in LocalRepository");
	}

	@Override
	public List<Drink> getFavoritesDrinks() {
		throw new UnsupportedOperationException("This method is supported only in LocalRepository");
	}

	@Override
	public Single<Integer> getFavoritesCount() {
		throw new UnsupportedOperationException("This method is supported only in LocalRepository");
	}

	@Override
	public Flowable<List<Drink>> getIngredients() {
		return getCocktailApi()
				.getIngredients()
				.map(ModelMapper::convertDrinksToList)
				.map(drinks -> {
					List<String> ingredients = new ArrayList<>(drinks.size());
					for (int i = 0; i < drinks.size(); i++) {
						ingredients.add("<item>" + drinks.get(i).getStrIngredient1() + "</item>\n");
					}
					Collections.sort(ingredients, String::compareToIgnoreCase);
					Timber.v("Drinks: %s", ingredients.toString());
					return drinks;
				})
				.subscribeOn(Schedulers.io());
	}

	@Override
	public Completable addToFavorites(Drink drink) {
		throw new UnsupportedOperationException("This method is supported only in LocalRepository");
	}

	@Override
	public Completable removeFromFavorites(long id) {
		throw new UnsupportedOperationException("This method is supported only in LocalRepository");
	}

	@Override
	public Completable reverseFavorite(long id) {
		throw new UnsupportedOperationException("This method is supported only in LocalRepository");
	}

	@Override
	public Completable updateDrinkHistory(long id, long time) {
		throw new UnsupportedOperationException("This method is supported only in LocalRepository");
	}

	@Override
	public Completable clearHistory() {
		throw new UnsupportedOperationException("This method is supported only in LocalRepository");
	}

	@Override
	public void clearAll() {
		throw new UnsupportedOperationException("This method is supported only in LocalRepository");
	}

	@Override
	public Completable removeFromHistory(long id) {
		throw new UnsupportedOperationException("This method is supported only in LocalRepository");
	}

	@Override
	public Single<Drink[]> cacheIntoLocalDatabase(Drinks drinks) {
		throw new UnsupportedOperationException("This method is supported only in LocalRepository");
	}

	@Override
	public Flowable<List<RatingDrink>> getRatingList() {
		throw new UnsupportedOperationException("This method is supported only in LocalRepository");
	}

	@Override
	public Completable replaceRating(List<RatingDrink> list) {
		throw new UnsupportedOperationException("This method is supported only in LocalRepository");
	}

	private CocktailApi getCocktailApi() {
		if (retrofit == null) {

			// Necessary because our servers don't have the right cipher suites.
			// https://github.com/square/okhttp/issues/4053
			// Error represents only on android 4.+
			List<CipherSuite> cipherSuites = new ArrayList<>();
			cipherSuites.addAll(ConnectionSpec.MODERN_TLS.cipherSuites());
			cipherSuites.add(CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA);
			cipherSuites.add(CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA);

			ConnectionSpec legacyTls = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
					.cipherSuites(cipherSuites.toArray(new CipherSuite[0]))
					.build();

			HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
			interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
			OkHttpClient client = new OkHttpClient.Builder()
					.connectionSpecs(Arrays.asList(legacyTls, ConnectionSpec.CLEARTEXT))
					.addInterceptor(interceptor)
					.connectTimeout(AppConstants.CONNECTION_TIMEOUT, TimeUnit.SECONDS)
					.readTimeout(AppConstants.READ_TIMEOUT, TimeUnit.SECONDS)
					.build();

			retrofit = new Retrofit.Builder()
					.baseUrl(API_URL)
					.client(client)
					.addConverterFactory(GsonConverterFactory.create())
					.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
					.build();
		}
		if (cocktailApi == null) {
			cocktailApi = retrofit.create(CocktailApi.class);
		}
		return cocktailApi;
	}

	interface CocktailApi {
		@GET("search.php")
		Flowable<Drinks> searchByName(@Query("s") String search);

		@GET("filter.php")
		Flowable<Drinks> searchByIngredient(@Query("i") String ingredient);

		@GET("filter.php")
		Flowable<Drinks> searchByCategory(@Query("c") String category);

		@GET("filter.php")
		Flowable<Drinks> searchByGlass(@Query("g") String glass);

		@GET("filter.php")
		Flowable<Drinks> searchByAlcoholic(@Query("a") String alcoholic);

		@GET("lookup.php")
		Flowable<Drinks> getCocktail(@Query("i") long id);

		@GET("list.php?i=list")
		Flowable<Drinks> getIngredients();

		@GET("random.php")
		Single<Drinks> getRandom();
	}
}
