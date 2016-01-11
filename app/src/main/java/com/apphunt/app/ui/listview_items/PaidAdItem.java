package com.apphunt.app.ui.listview_items;

import com.apphunt.app.constants.Constants;

/**
 * Created by nmp on 16-1-11.
 */
public class PaidAdItem implements Item  {
        @Override
        public Constants.ItemType getType() {
            return Constants.ItemType.PAID_AD;
        }
}
