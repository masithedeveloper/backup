/*
 *  Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
 *
 *  This code is confidential to Absa Bank Limited and shall not be disclosed
 *  outside the Bank without the prior written permission of the Absa Legal
 *
 *  In the event that such disclosure is permitted the code shall not be copied
 *  or distributed other than on a need-to-know basis and any recipients may be
 *  required to sign a confidentiality undertaking in favor of Absa Bank
 *  Limited
 *
 */

package styleguide.forms;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import styleguide.utils.extensions.StringExtensions;
import za.co.absa.presentation.uilib.R;
import za.co.absa.presentation.uilib.databinding.ActivityMultiSelectBinding;

public class MultiSelectCheckBoxActivity extends AppCompatActivity implements AdditionalDamagesItemAdapter.AdditionalItemCheckListener {
    public static final String TOOL_BAR_TITLE = "toolBarTitle";
    List<MultiSelectItem> selectedItemList;
    public static final String ADDITIONAL_DAMAGES = "additionalDamages";
    public static final String ITEMS_LIST = "itemList";
    private List<Integer> selectedItemIndexList;
    public static String PREVIOUSLY_SELECTED_ITEMS = "previouslySelectedItems";
    ActivityMultiSelectBinding binding;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_multi_select, null, false);
        setContentView(binding.getRoot());

        Toolbar toolbar = findViewById(R.id.toolbar);
        selectedItemIndexList = new ArrayList<>();
        Intent intent = getIntent();
        if (intent.hasExtra(ITEMS_LIST)) {
            setSupportActionBar(toolbar);
            toolbar.setTitle(StringExtensions.toTitleCase(getIntent().getStringExtra(TOOL_BAR_TITLE)));
            if (getSupportActionBar() != null) {
                getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_left_dark);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            selectedItemList = (List<MultiSelectItem>) getIntent().getSerializableExtra(ITEMS_LIST);
            AdditionalDamagesItemAdapter additionalDamagesItemAdapter = new AdditionalDamagesItemAdapter(selectedItemList, this);
            binding.additionalDamagesItemsRecyclerView.setAdapter(additionalDamagesItemAdapter);
            binding.additionalDamagesItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            if (intent.hasExtra(PREVIOUSLY_SELECTED_ITEMS)) {
                selectedItemIndexList = (List<Integer>) intent.getSerializableExtra(PREVIOUSLY_SELECTED_ITEMS);
                additionalDamagesItemAdapter.retainPreviouslyCheckedItems(selectedItemIndexList);
                binding.doneSelectingButton.setEnabled(true);
            }
        }

        binding.doneSelectingButton.setOnClickListener(v -> {
            if (selectedItemList != null && !selectedItemList.isEmpty()) {
                Intent resultsIntent = new Intent();
                resultsIntent.putExtra(ADDITIONAL_DAMAGES, (Serializable) selectedItemIndexList);
                setResult(Activity.RESULT_OK, resultsIntent);
                finish();
            } else {
                finish();
            }
        });
    }

    @Override
    public void onItemChecked(int position, boolean isChecked) {
        if (isChecked) {
            selectedItemIndexList.add(position);
        } else {
            int indexOfItemClicked = selectedItemIndexList.indexOf(position);
            selectedItemIndexList.remove(indexOfItemClicked);
        }
        if (selectedItemIndexList != null && !selectedItemIndexList.isEmpty()) {
            binding.doneSelectingButton.setEnabled(true);
        } else {
            binding.doneSelectingButton.setEnabled(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NotNull MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}
