package com.cureme.data;

import com.cureme.activities.CureMeConstants;

import java.util.ArrayList;

public class CureMeDataSource {

	private static CureMeDataSource cureMeDataSrc = new CureMeDataSource();
	
	private ArrayList<CureMeGroup> groups = new ArrayList<CureMeGroup>();

	/**
	 * @return the groups
	 */
	public ArrayList<CureMeGroup> getGroups() {
		return groups;
	}
	
	public static CureMeGroup GetGroup (String id){
		for(CureMeGroup group : cureMeDataSrc.getGroups()){
	        if(group.get_title() != null && group.get_title().contains(id)){
	           return group ;
	        }
	    }
		return null;
	}
	
	public static CureMeItem GetItem (CureMeGroup group, String id){
		for(CureMeItem item : group.getItems()){
	        if(item.get_title() != null && item.get_title().contains(id)){
	           return item ;
	        }
	    }
		return null;
	}
	
	public CureMeDataSource(){
		CureMeGroup group1 = new CureMeGroup("SelfCure",
                CureMeConstants.SELF_CURE,
                "Self cure will provide you with description of problem, its causes, possible symptoms, things you can can do to avoid it, and also most importantly things which you can do to cure it easily at home. ",
                CureMeConstants.PIC_SELFCURE
                );
		
		this.groups.add(group1);
		
		CureMeGroup group2 = new CureMeGroup("Seasonal",
				CureMeConstants.SEASONAL_PROBLEM,
                "There are certain health problems which are seasonal. In order to fight against such seasonal problems one needs to be careful and follow simple steps to stay healthy and enjoy the season.",
                CureMeConstants.PIC_SEASONALPROBLEM);
        this.groups.add(group2);

        CureMeGroup group3 = new CureMeGroup("FirstAid",
        		CureMeConstants.FIRST_AID,
                "What would you do when there is emergency and medical help is taking too long to come by? Don't worry. This section describes all medical help which you could require in case of emergency.",
                CureMeConstants.PIC_FIRSTAID);
        this.groups.add(group3);

        CureMeGroup group4 = new CureMeGroup("Granny",
        		CureMeConstants.GRANNY_TIPS,
                "We all have followed our granny's tips sometimes in our lifetime and might have found it to be better than medicines at times. So here are all those tips which can be easily followed at home without having any side effects.",
                CureMeConstants.PIC_GRANNYTIPS);
        this.groups.add(group4);

        CureMeGroup group5 = new CureMeGroup("ContactDoctor",
        		CureMeConstants.CONTACT_DOCTOR,
                "In case you cannot find help for your problems from the solution we have provided then you have an option to contact our doctors team. You can describe your problem to us and we will get back to you.",
                CureMeConstants.PIC_CONTACTDOCTOR);
        this.groups.add(group5);

        CureMeGroup group6 = new CureMeGroup("NearestHelp",
                CureMeConstants.NEAREST_HELP,
                "All the doctors, pharmacies and hospitals are listed below. Get the nearest help you require.",
                CureMeConstants.PIC_NEARESTHELP);
        this.groups.add(group6);

	}

}
