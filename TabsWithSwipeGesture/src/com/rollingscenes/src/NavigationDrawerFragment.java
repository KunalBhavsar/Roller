
package com.rollingscenes.src;

import java.lang.reflect.Field;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.rollingscenes.R;
import com.rollingscenes.src.utils.KeyConstants;
import com.rollingscenes.src.utils.SharedPrefs;

/**
 * Fragment class for creating Navigation drawer.
 * @author Neebal Technologies
 * 
 */
public class NavigationDrawerFragment extends Fragment implements OnClickListener {
	
	/**
	 * Helper component that ties the action bar to the navigation drawer.
	 */
	private ActionBarDrawerToggle mDrawerToggle;
	/**
	 * Stores fragment UI.
	 */
	private DrawerLayout mDrawerLayout;
	/**
	 * Represent expandable list view of the items of navigation drawer
	 */
	private ExpandableListView myExpandableListView;
	
	private Activity activity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// Indicate that this fragment would like to influence the set of
		// actions in the action bar.
		setHasOptionsMenu(true);
	}

	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View mDrawerRelativeLayout = (View) inflater.inflate(
				R.layout.fragment_navigation_drawer, container, false);

			
		return mDrawerRelativeLayout;
	}

	@SuppressLint("NewApi")
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		//Represent the expandable list view
		myExpandableListView = (ExpandableListView) view
				.findViewById(R.id.exp_my_groups);
		
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
		final AlertDialog dialog = dialogBuilder.setMessage("Do you want to Logout?")
		.setPositiveButton("YES", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				logout();
			}
		})
		.setNegativeButton("NO", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		}).create();

		((TextView)view.findViewById(R.id.txt_logout)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mDrawerLayout.closeDrawers();
				dialog.show();
			}
		});
		
		//set the adapter for showing list of items
		notifyDrawerAdapter();
		
		activity = getActivity();
	}
	//reset the navigation drawer (in case any new resource of request get out dated)
	public void notifyDrawerAdapter() {
		
		//for setting the manual group indicator remove the default one
		myExpandableListView.setGroupIndicator(null);
		
		//get the list of groups and its childs from key constants 
		String[] myGroup = {
				KeyConstants.CATEGORY_FOOD_DRINKS, KeyConstants.CATEGORY_COMEDY, KeyConstants.CATEGORY_MUSIC,
				KeyConstants.CATEGORY_OUTDOOR, KeyConstants.CATEGORY_NIGHTLIFE, KeyConstants.CATEGORY_HAPPY_HOURS, 
				KeyConstants.CATEGORY_CULTURE, KeyConstants.CATEGORY_THEATER};
		
		ArrayList<CategoryAndItsColor> categories = new ArrayList<>();
		
		categories.add(new CategoryAndItsColor(KeyConstants.CATEGORY_FOOD_DRINKS, "#FC5B3F"));
		categories.add(new CategoryAndItsColor(KeyConstants.CATEGORY_COMEDY, "#FCB03C"));
		categories.add(new CategoryAndItsColor(KeyConstants.CATEGORY_MUSIC, "#FF1939"));
		categories.add(new CategoryAndItsColor(KeyConstants.CATEGORY_OUTDOOR, "#52A5D9"));
		categories.add(new CategoryAndItsColor(KeyConstants.CATEGORY_NIGHTLIFE, "#BB2326"));
		categories.add(new CategoryAndItsColor(KeyConstants.CATEGORY_HAPPY_HOURS, "#D7184E"));
		categories.add(new CategoryAndItsColor(KeyConstants.CATEGORY_CULTURE, "#56BF66"));
		categories.add(new CategoryAndItsColor(KeyConstants.CATEGORY_THEATER, "#5EE3F2"));
		
		String [][] mySubitems = {
			{},{},{},
			{},{},{},
			{},{}
		};
		

		//Represent the expandable list adapter
		ExpandableListAdapter expandableListAdapter = new ExpandableListAdapter(categories,
				mySubitems,true);
		//set the adapter on expandable list view
		myExpandableListView.setAdapter(expandableListAdapter);
		
		//set the click listener on child and groups
		//myExpandableListView.setOnChildClickListener(myGroupItemClicked);
		myExpandableListView.setOnGroupClickListener(myGroupClicked);
	}
	
	/**
	 * group click listener for expandable list view
	 */
	private OnGroupClickListener myGroupClicked = new OnGroupClickListener() {
		
		@Override
		public boolean onGroupClick(ExpandableListView parent, View v,
				int groupPosition, long id) {
			Intent intent = new Intent(getActivity().getApplicationContext(), EventsByCategoryActivity.class);
			
			if(groupPosition == 0) { //Food + Drinks
				intent.putExtra(KeyConstants.SELECTED_CATEGORY, KeyConstants.CATEGORY_FOOD_DRINKS);
				//intent.putExtra(KeyConstants.SELECTED_CATEGORY, "Food%20+%20Drinks");
				intent.putExtra(KeyConstants.SELECTED_CATEGORY_COLOR, "#FC5B3F");
				mDrawerLayout.closeDrawers();
			}
			else if(groupPosition == 1) { //Comedy
				intent.putExtra(KeyConstants.SELECTED_CATEGORY, KeyConstants.CATEGORY_COMEDY);
				//intent.putExtra(KeyConstants.SELECTED_CATEGORY, "Comedy");
				intent.putExtra(KeyConstants.SELECTED_CATEGORY_COLOR, "#FCB03C");
				mDrawerLayout.closeDrawers();
			}
			else if(groupPosition == 2) { //Music
				intent.putExtra(KeyConstants.SELECTED_CATEGORY, KeyConstants.CATEGORY_MUSIC);
				//intent.putExtra(KeyConstants.SELECTED_CATEGORY, "Music");
				intent.putExtra(KeyConstants.SELECTED_CATEGORY_COLOR, "#FF1939");
				mDrawerLayout.closeDrawers();
			}
			else if(groupPosition == 3) { //Outdoor
				intent.putExtra(KeyConstants.SELECTED_CATEGORY, KeyConstants.CATEGORY_OUTDOOR);
				//intent.putExtra(KeyConstants.SELECTED_CATEGORY, "Outdoor");
				intent.putExtra(KeyConstants.SELECTED_CATEGORY_COLOR, "#52A5D9");
				mDrawerLayout.closeDrawers();
			}
			else if(groupPosition == 4) { //Nightlife
				intent.putExtra(KeyConstants.SELECTED_CATEGORY, KeyConstants.CATEGORY_NIGHTLIFE);
				//intent.putExtra(KeyConstants.SELECTED_CATEGORY, "Nightlife");
				intent.putExtra(KeyConstants.SELECTED_CATEGORY_COLOR, "#BB2326");
				mDrawerLayout.closeDrawers();
			}
			else if(groupPosition == 5) { //HappyHours
				intent.putExtra(KeyConstants.SELECTED_CATEGORY, KeyConstants.CATEGORY_HAPPY_HOURS);
				//intent.putExtra(KeyConstants.SELECTED_CATEGORY, "Happy%20Hours");
				intent.putExtra(KeyConstants.SELECTED_CATEGORY_COLOR, "#D7184E");
				mDrawerLayout.closeDrawers();
			}
			else if(groupPosition == 6) { //Culture
				intent.putExtra(KeyConstants.SELECTED_CATEGORY, KeyConstants.CATEGORY_CULTURE);
				//intent.putExtra(KeyConstants.SELECTED_CATEGORY, "Culture");
				intent.putExtra(KeyConstants.SELECTED_CATEGORY_COLOR, "#56BF66");
				mDrawerLayout.closeDrawers();
			}
			else if(groupPosition == 7) { //Theater
				intent.putExtra(KeyConstants.SELECTED_CATEGORY, KeyConstants.CATEGORY_THEATER);
				//intent.putExtra(KeyConstants.SELECTED_CATEGORY, "Theatre");
				intent.putExtra(KeyConstants.SELECTED_CATEGORY_COLOR, "#5EE3F2");
				mDrawerLayout.closeDrawers();
			}
			
			startActivity(intent);
			
			return false;
		}
	};
	
    private void logout() {
    	SharedPrefs sPrefs = new SharedPrefs(activity.getApplicationContext());
    	Intent intent = new Intent(activity.getApplicationContext(), SocialLoginActivity.class);
    	sPrefs.setLoggedIn(false);
    	sPrefs.setLogoutClicked(true);
    	startActivity(intent);
    	activity.finish();
	}

	/**
	 * This class holds the views required for the Expandable list adapter
	 * @author Neebal Technologies
	 */
	class MyExpandableItemsViewHolder {
		private TextView text;
		private ImageView expIcon;
	}
	/**
	 * Expandable list adapter for showing expanded sublist on clicking main
	 * list item.
	 * @author Neebal Technologies
	 */
	public class ExpandableListAdapter extends BaseExpandableListAdapter {

		private final LayoutInflater inf;
		private ArrayList<CategoryAndItsColor> categories;
		private String[][] children;
		private Typeface helveticaLight;
		
		public ExpandableListAdapter(ArrayList<CategoryAndItsColor> categories, String[][] children,boolean isMyCalendar) {
			helveticaLight = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(),
		            "fonts/helvetica_light.otf");
			this.categories = categories;
			this.children = children;
			inf = LayoutInflater.from(getActivity());
		}

		@Override
		public int getGroupCount() {
			return categories.size();
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return children[groupPosition].length;
		}

		@Override
		public Object getGroup(int groupPosition) {
			return categories.get(groupPosition);
		}
		
		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return children[groupPosition][childPosition];
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public View getChildView(int groupPosition, final int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			
			View view = inf.inflate(R.layout.list_nav_item, parent, false);
				
			//get the views from view holder
			MyExpandableItemsViewHolder myExpandableItemsViewHolder = new MyExpandableItemsViewHolder();
			
			myExpandableItemsViewHolder.text = (TextView) view.findViewById(R.id.txt_list_item);
			
			myExpandableItemsViewHolder.text.setTypeface(helveticaLight);
			
			//get the string child view text and set it on text view
			String itemText = getChild(groupPosition, childPosition).toString();
			myExpandableItemsViewHolder.text.setText(itemText);
			
			//set the devider of 1st child of each group as invisible
			if(childPosition == 0) {
				((View) view.findViewById(R.id.item_devider_of_navigation_drawer)).setVisibility(View.INVISIBLE);
			}
			return view;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			
			convertView = inf.inflate(R.layout.list_nav_group, parent, false);
			
			//get the views from view holder class
			MyExpandableItemsViewHolder myExpandableItemsViewHolder = new MyExpandableItemsViewHolder();
			myExpandableItemsViewHolder.expIcon = (ImageView)convertView.findViewById(R.id.img_exp_icon);
			
			myExpandableItemsViewHolder.text = (TextView) convertView.findViewById(R.id.txt_list_header);
			myExpandableItemsViewHolder.text.setTypeface(helveticaLight);
			//set the text from string passed to group view
			CategoryAndItsColor categoryAndItsColor = (CategoryAndItsColor)getGroup(groupPosition);
			myExpandableItemsViewHolder.text.setText(categoryAndItsColor.toString());
			myExpandableItemsViewHolder.text.setTextColor(Color.parseColor(categoryAndItsColor.getColor()));
			
			//set the visibility of divider to invisible if group view is first or last
			if(groupPosition == 0 || groupPosition == 7) {
				((View)convertView.findViewById(R.id.group_devider_of_navigation_drawer_bottom)).setVisibility(View.VISIBLE);
			}
			//else set visibilty to visible
			else {
				((View)convertView.findViewById(R.id.group_devider_of_navigation_drawer_bottom)).setVisibility(View.INVISIBLE);
			}
			
			//if children count is 0 then set the drawable of expandable icon to null
			if(getChildrenCount(groupPosition) == 0) {
				myExpandableItemsViewHolder.expIcon.setImageDrawable(null);
			}
			else {
			//else set the open/close expandable icon
				if(isExpanded) {
					myExpandableItemsViewHolder.expIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_collapse));
				}
				else {
					myExpandableItemsViewHolder.expIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_expand));
				}
			}
			return convertView;
		}
		
		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
	}
	
	/**
	 * Users of this fragment must call this method to set up the navigation
	 * drawer interactions.
	 * 
	 * @param fragmentId
	 *            The android:id of this fragment in its activity's layout.
	 * @param drawerLayout
	 *            The DrawerLayout containing this fragment's UI.
	 */
	public void setUp(int fragmentId, DrawerLayout drawerLayout) {
		
		mDrawerLayout = drawerLayout;
		// set a custom shadow that overlays the main content when the drawer
		// opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		
		//remove the app icon from action bar and set the navigation drawer button
		ActionBar actionBar = ((FragmentActivity) getActivity()).getActionBar();
		actionBar.setHomeButtonEnabled(true);
	    actionBar.setDisplayHomeAsUpEnabled(true);
	    actionBar.setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
	    actionBar.setDisplayShowTitleEnabled(true);
	    
		// ActionBarDrawerToggle ties together the the proper interactions
		// between the navigation drawer and the action bar app icon.
		mDrawerToggle = new ActionBarDrawerToggle(getActivity(),// host Activity
				mDrawerLayout,// DrawerLayout object
				R.drawable.ic_drawer, // nav drawer image to replace 'Up' caret
				R.string.navigation_drawer_open,// "open drawer" description for
												// accessibility
				R.string.navigation_drawer_close // "close drawer" description
													// for accessibility
		) {
			@Override
			public void onDrawerClosed(View drawerView) {
				//called when drawer is closed
				super.onDrawerClosed(drawerView);
				if (!isAdded()) {
					return;
				}
				
				//set the action bar
				ActionBar actionBar = ((FragmentActivity) getActivity())
						.getActionBar();
				actionBar.setHomeButtonEnabled(true);
			    actionBar.setDisplayHomeAsUpEnabled(true);
			    actionBar.setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
			    actionBar.setDisplayShowTitleEnabled(true);

				getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				//called when drawer is opened
				super.onDrawerOpened(drawerView);
				if (!isAdded()) {
					return;
				}
				InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus()
										.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
				
				//set the action bar
				ActionBar actionBar = ((FragmentActivity) getActivity())
						.getActionBar();
				actionBar.setHomeButtonEnabled(true);
			    actionBar.setDisplayHomeAsUpEnabled(true);
			    actionBar.setIcon(getResources().getDrawable(R.drawable.ic_launcher));
			    actionBar.setDisplayShowTitleEnabled(false);
			    
			    getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
			}
		};
		
		// Defer code dependent on restoration of previous instance state.
		mDrawerLayout.post(new Runnable() {
			@Override
			public void run() {
				mDrawerToggle.syncState();
			}
		});

		mDrawerLayout.setDrawerListener(mDrawerToggle);
		getOverflowMenu();
	}
	
	private void getOverflowMenu() {

	     try {
	        ViewConfiguration config = ViewConfiguration.get(getActivity().getApplicationContext());
	        Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
	        if(menuKeyField != null) {
	            menuKeyField.setAccessible(true);
	            menuKeyField.setBoolean(config, false);
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

	}

	@Override
	public void onDetach() {
		super.onDetach();

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Forward the new configuration the drawer toggle component.
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onClick(View v) {
	}
	
	public class CategoryAndItsColor {
		private String category;
		private String color;
		public CategoryAndItsColor(String category, String color) {
			super();
			this.category = category;
			this.color = color;
		}
		public String getCategory() {
			return category;
		}
		public String getColor() {
			return color;
		}
		
		@Override
		public String toString() {
			return category;
		}
	}
}
