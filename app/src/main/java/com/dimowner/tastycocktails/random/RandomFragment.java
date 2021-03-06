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

package com.dimowner.tastycocktails.random;

import android.app.ActivityOptions;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import javax.inject.Inject;

import com.dimowner.tastycocktails.AdvHandler;
import com.dimowner.tastycocktails.R;
import com.dimowner.tastycocktails.TCApplication;
import com.dimowner.tastycocktails.cocktails.details.ImagePreviewActivity;
import com.dimowner.tastycocktails.cocktails.details.IngredientsAdapter;
import com.dimowner.tastycocktails.dagger.random.RandomCocktailModule;
import com.dimowner.tastycocktails.data.Prefs;
import com.dimowner.tastycocktails.util.AndroidUtils;
import com.dimowner.tastycocktails.util.AnimationUtil;
import com.google.android.gms.ads.AdView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

/**
 * Created on 27.07.2017.
 * @author Dimowner
 */
public class RandomFragment extends Fragment {

//	private final String EXTRAS_KEY_ADAPTER_DATA = "adapter_data";
	public final static String TAG = "RandomFragment";

	@Inject
	RandomContract.UserActionsListener mPresenter;

	@Inject
	Prefs prefs;

	private RecyclerView mRecyclerView;
	private IngredientsAdapter mAdapter;

	private boolean isFavorite;
	private boolean isCreated;

	private ImageButton btnMenu;
	private ImageButton btnFavorite;
	private FloatingActionButton fab;
	private CoordinatorLayout mRoot;
	private AdvHandler advHandler;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_random, container, false);
		mRecyclerView = view.findViewById(R.id.recycler_view);
		TCApplication.get(getContext()).applicationComponent()
				.plus(new RandomCocktailModule(this)).injectDetailsFragment(this);
		return view;
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mRecyclerView.setHasFixedSize(true);
		mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

		mRoot = view.findViewById(R.id.coordinator);
		btnFavorite = view.findViewById(R.id.btn_favorite);
		btnMenu = view.findViewById(R.id.btn_menu);

		btnMenu.setOnClickListener(v -> {
			DrawerLayout dl = getActivity().findViewById(R.id.drawer_layout);
			if (dl != null) {
				dl.openDrawer(Gravity.START);
			}
		});
		fab = view.findViewById(R.id.fab);
		fab.setOnClickListener(v -> {
				mPresenter.loadRandomDrink();
			});

		btnFavorite.setOnClickListener(v -> mPresenter.reverseFavorite());

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			// Set the padding to match the Status Bar height
			view.findViewById(R.id.title_bar).setPadding(0, AndroidUtils.getStatusBarHeight(getContext()), 0, 0);
		}

		if (savedInstanceState == null) {
			initAdapter();

			mPresenter.bindView(mAdapter);
		}

		AdView adView = view.findViewById(R.id.adView);
		advHandler = new AdvHandler(adView, prefs);

	}

	@Override
	public void onResume() {
		super.onResume();
		advHandler.onResume();
		if (!isCreated) {
			mPresenter.loadRandomDrink();
			fab.setVisibility(View.VISIBLE);
			AnimationUtil.physBasedRevealAnimation(fab);
			isCreated = true;
		}
	}

	@Override
	public void onPause() {
		advHandler.onPause();
		super.onPause();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		updateFavorite(isFavorite);
	}

	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean("is_favorite", isFavorite);
		outState.putBoolean("is_created", isCreated);
		if (mAdapter != null) {
//			outState.putParcelable(EXTRAS_KEY_ADAPTER_DATA, mAdapter.onSaveInstanceState());
			mAdapter.onSaveAdapterState(outState);
		}
	}

	@Override
	public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
		super.onViewStateRestored(savedInstanceState);
		if (savedInstanceState != null) {
			isFavorite = savedInstanceState.getBoolean("is_favorite");
			isCreated = savedInstanceState.getBoolean("is_created");
//			isCreated = false;
			updateFavorite(isFavorite);
			initAdapter();

			mPresenter.bindView(mAdapter);
//			mAdapter.onRestoreInstanceState(savedInstanceState.getParcelable(EXTRAS_KEY_ADAPTER_DATA));
			mAdapter.onRestoreAdapterSate(savedInstanceState);
			fab.setVisibility(View.VISIBLE);
		}
	}

	private void initAdapter() {
		if (mAdapter == null) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
				mAdapter = new IngredientsAdapter(getActivity().isInMultiWindowMode());
			} else {
				mAdapter = new IngredientsAdapter();
			}

			mRecyclerView.setAdapter(mAdapter);
		}

		mAdapter.setItemClickListener((view1, position) ->
				startIngredientDetailsActivity(mAdapter.getItem(position).getImageUrl()));

		mAdapter.setFavoriteUpdateListener(fav -> {
			isFavorite = fav;
			updateFavorite(isFavorite);
		});

		mAdapter.setOnImageClickListener(this::startIngredientDetailsActivity);

		btnFavorite.setImageResource(isFavorite ? R.drawable.round_heart: R.drawable.round_heart_border);

		mAdapter.setOnSnackBarListener(message -> Snackbar.make(mRoot, message, Snackbar.LENGTH_LONG).show());
	}

	@Override
	public void onDestroyView() {
		advHandler.onDestroy();
		super.onDestroyView();
		mPresenter.unbindView();
		mPresenter = null;
	}

	private void startIngredientDetailsActivity(String url) {
		if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			startActivity(ImagePreviewActivity.getStartIntent(getContext(), url),
					ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
		} else {
			startActivity(ImagePreviewActivity.getStartIntent(getContext(), url));
		}
	}

	private void updateFavorite(boolean fav) {
//		if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			btnFavorite.setImageResource(fav ? R.drawable.round_heart : R.drawable.round_heart_border);
//		} else {
//			btnFavorite.setImageResource(fav ? R.drawable.heart: R.drawable.heart_outline);
//		}
	}
}
