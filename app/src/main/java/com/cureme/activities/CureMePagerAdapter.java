package com.cureme.activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class CureMePagerAdapter extends FragmentPagerAdapter {

    private List<? extends Fragment> fragments; 
    /** 
     * @param fm 
     * @param fragments 
     */
    public CureMePagerAdapter(FragmentManager fm, List<? extends Fragment> fragments) { 
        super(fm); 
        this.fragments = fragments; 
    } 
    /* (non-Javadoc) 
     * @see android.support.v4.app.FragmentPagerAdapter#getItem(int) 
     */ 
    @Override 
    public Fragment getItem(int position) { 
        return this.fragments.get(position); 
    } 
  
    /* (non-Javadoc) 
     * @see android.support.v4.view.PagerAdapter#getCount() 
     */
    @Override
    public int getCount() { 
        return this.fragments.size(); 
    } 


}
