package com.onescream.intros;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Page adapter class for Swipe-able pages of First Screen
 *
 * Created by Anwar Almojarkesh
 */

class FirstScreensAdapter extends FragmentPagerAdapter {
    private int mCount = 5;

    private Fragment[] m_fragments = new Fragment[mCount];
   private boolean m_bInTour;
    private boolean m_bInsignup;
    Context mcontext;





    public FirstScreensAdapter(FragmentManager fm, boolean p_bOnlyFirstScreen,boolean m_bInTour,Context cnx) {
        super(fm);
        this.m_bInTour=m_bInTour;
        this.m_bInsignup=p_bOnlyFirstScreen;
        this.mcontext=cnx;
//        if (p_bOnlyFirstScreen)
//            mCount = 1;

        if(m_bInTour)
        {
            mCount=4;
            if(m_bInsignup)
            {
                mCount=1;

            }
        }




    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;

        if (m_fragments[position] != null)
            return m_fragments[position];

        if(!m_bInTour)
        {
               if (position == 0) {
                    fragment = new Page1Fragment();
                } else if (position == 1) {
                    fragment = new Page2Fragment();
                } else if (position == 2) {
                    fragment = new Page3Fragment();
                } else if (position == 3) {
                    fragment = new Page4Fragment();
                }
//               else if (position == 4) {
//                    fragment = new Page5Fragment();
//                }
               else if (position == 4) {
                   fragment = new RegisterFragment();
               }
        }
        
        else{
            if(m_bInsignup){
                fragment = new Page5Fragment();
            }else {
                if (position == 0) {
                        fragment = new Page2Fragment();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("activity", m_bInTour);
                    fragment.setArguments(bundle);

//                try {
//                    // Instantiate the Listener so we can send events to the host
//                    IListener2 = (indicatorListener2)mcontext;
//                } catch (ClassCastException e) {
//                    // The activity doesn't implement the interface, throw exception
//                    throw new ClassCastException(mcontext.toString()
//                            + " must implement cadlrDialogListener");
//                }
//                IListener2.activity(1);

                } else if (position == 1) {
                    fragment = new Page3Fragment();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("activity", m_bInTour);
                    fragment.setArguments(bundle);
//                try {
//                    // Instantiate the Listener so we can send events to the host
//                    IListener3 = (indicatorListener3)mcontext;
//                } catch (ClassCastException e) {
//                    // The activity doesn't implement the interface, throw exception
//                    throw new ClassCastException(mcontext.toString()
//                            + " must implement cadlrDialogListener");
//                }
//                IListener3.activity(1);

                } else if (position == 2) {
                    fragment = new Page4Fragment();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("activity", m_bInTour);
                    fragment.setArguments(bundle);
//                try {
//                    // Instantiate the Listener so we can send events to the host
//                    IListener4 = (indicatorListener4)mcontext;
//                } catch (ClassCastException e) {
//                    // The activity doesn't implement the interface, throw exception
//                    throw new ClassCastException(mcontext.toString()
//                            + " must implement cadlrDialogListener");
//                }
//                IListener4.activity(1);

                } else if (position == 3) {
                    fragment = new Page5Fragment();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("activity", m_bInTour);
                    fragment.setArguments(bundle);
//                try {
//                    // Instantiate the Listener so we can send events to the host
//                    IListener5 = (indicatorListener5)mcontext;
//                } catch (ClassCastException e) {
//                    // The activity doesn't implement the interface, throw exception
//                    throw new ClassCastException(mcontext.toString()
//                            + " must implement cadlrDialogListener");
//                }
//                IListener5.activity(1);

                }
            }
        }
        m_fragments[position] = fragment;
        return fragment;
    }

    @Override
    public int getCount() {
        return mCount;
    }
}