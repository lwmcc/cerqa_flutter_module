package com.cerqa.ui.components

import com.cerqa.ui.Navigation.BottomNavItem
import com.cerqa.ui.Navigation.TopNavItem

/**
 * Main bottom navigation bar items
 */
expect val navItems: List<BottomNavItem>

/**
 * Main top navigation bar items
 */
expect val topNavItemsMain: List<TopNavItem>

expect val topNavItemsContacts: List<TopNavItem>

expect val topNavItemsGroups: List<TopNavItem>