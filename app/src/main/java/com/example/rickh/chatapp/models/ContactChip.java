package com.example.rickh.chatapp.models;

import android.content.Context;
import android.support.design.chip.Chip;

import com.example.rickh.chatapp.R;

public class ContactChip extends Chip {

    private int contactId;

    public ContactChip(Context context, int contactId) {
        super(context);
        this.contactId = contactId;
    }

    public int getContactId() {
        return contactId;
    }

    @Override
    public void setTextAppearance(int resId) {
        super.setTextAppearance(resId);
    }
}
