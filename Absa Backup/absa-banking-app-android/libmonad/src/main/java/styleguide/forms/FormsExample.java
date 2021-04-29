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

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import za.co.absa.presentation.uilib.R;

public class FormsExample extends AppCompatActivity {

    private CheckBoxView checkBox1;
    private CheckBoxView checkBox2;
    private CheckBoxView checkBox3;
    private Button validateButton;
    private RadioButtonView<Person> radioButtonView;
    private RadioButtonView<Person> radioButtonView2;
    private RadioButtonView<Person> radioButtonView3;
    private KeypadView keypadView;

    private LargeInputView<AccountExample> largeInputView;
    private RoundedSelectorView<StringItem> roundedSelectorView;
    private RoundedSelectorView<AccountObjectExample> longRoundedSelectorView;
    private NormalInputView<StringItem> normalInputView;
    private NormalInputView<StringItem> searchableNormalInputView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forms_example);

        initViews();

        wireUpAndSetDataRadioButtonViewComponents();
        wireUpAndSetDataNormalInputViewComponents();

        //testLongRoundedSelectorView();
    }

    void testLongRoundedSelectorView() {
/*        longRoundedSelectorView = findViewById(R.id.longRoundedSelectorView);
        longRoundedSelectorView.setHintText("Select account");
        //longRoundedSelectorView.getEditTExt().setHintTextColor(Color.GRAY);
        SelectorList<AccountObjectExample> items = new SelectorList<>();
        items.add(new AccountObjectExample("Current account", "4901 6783 367"));
        items.add(new AccountObjectExample("Credit account", "4901 7485 567"));
        items.add(new AccountObjectExample("Savings account", "4901 3456 678"));
        longRoundedSelectorView.setList(items, "Select Account");*/

    }

    private void initViews() {
        SelectorList<StringItem> stringItems = new SelectorList<>();
        for (int i = 0; i < 12; i++) {
            stringItems.add(new StringItem("Item " + i));
        }

        SelectorList<StringItem> stringItems2 = new SelectorList<>();
        for (int i = 0; i < 5; i++) {
            stringItems2.add(new StringItem("Item " + i));
        }

        SelectorList<AccountExample> accountExamples = new SelectorList<>();
        AccountExample accountExample1 = new AccountExample();
        accountExample1.setCardHolderName("Shane");
        accountExample1.setCardNumber("4832 8484 8484 8484");

        AccountExample accountExample2 = new AccountExample();
        accountExample2.setCardHolderName("Tummie");
        accountExample2.setCardNumber("2832 8484 8484 8484");

        AccountExample accountExample3 = new AccountExample();
        accountExample3.setCardHolderName("Tash");
        accountExample3.setCardNumber("1832 8484 8484 8484");

        accountExamples.add(accountExample1);
        accountExamples.add(accountExample2);
        accountExamples.add(accountExample3);

        //roundedSelectorView = findViewById(R.id.rounded_selector_view);
        //roundedSelectorView.setList(stringItems, "Rounded Selector View");

        normalInputView = findViewById(R.id.normal_input_view);
        normalInputView.addValueViewTextWatcher(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Toast.makeText(getApplicationContext(), "Text changed ...,", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //normalInputView.setList(stringItems2, 5002, "Normal Input View");

        largeInputView = findViewById(R.id.large_input_view);
        largeInputView.setList(accountExamples, "Accounts");

        checkBox1 = findViewById(R.id.check_box2);
        checkBox2 = findViewById(R.id.check_box1);
        checkBox3 = findViewById(R.id.check_box3);

        validateButton = findViewById(R.id.validate_button);

        validateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBox1.validate();
                checkBox2.validate();
                checkBox3.validate();

                radioButtonView.validate();
                radioButtonView2.validate();
            }
        });

        keypadView = findViewById(R.id.keypad_view);
        keypadView.changeForgotPasscodeText("Try Again!");
        keypadView.setForgotPasscodeOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Clicked!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkBox3.setChecked(false);
        checkBox3.setErrorMessage("lskajhdlksajhdlaksj");
        checkBox3.setDescription("This is set from code");
        checkBox3.setIsRequired(true);
    }

    private void wireUpAndSetDataRadioButtonViewComponents() {
        radioButtonView = findViewById(R.id.radio_button_view1);
        radioButtonView2 = findViewById(R.id.radio_button_view2);
        radioButtonView3 = findViewById(R.id.radio_button_view3);

        Person person1 = new Person();
        person1.name = "Shane";
        person1.surname = "Sardinha";

        Person person2 = new Person();
        person2.name = "CJ";
        person2.surname = "Guy";

        Person person3 = new Person();
        person3.name = "Description 5";
        person3.surname = "Guy";

        Person person4 = new Person();
        person4.name = "Description 2";
        person4.surname = "Guy";

        Person person5 = new Person();
        person5.name = "Description 3";
        person5.surname = "Guy";

        SelectorList<Person> personList = new SelectorList<>();
        personList.add(person1);
        personList.add(person2);
        personList.add(person3);
        personList.add(person4);
        personList.add(person5);

        radioButtonView.setDataSource(personList);
        radioButtonView2.setDataSource(personList, 1);

        radioButtonView3.setDataSource(personList);
        radioButtonView3.setSelectedIndex(3);
    }

    private void wireUpAndSetDataNormalInputViewComponents() {
        searchableNormalInputView = findViewById(R.id.searchable_normal_input_view);

        StringItem item1 = new StringItem("Shane", "Sardinha");
        StringItem item2 = new StringItem("Prateek", "Sharma");
        StringItem item3 = new StringItem("Tashinga", "Pemhiwa");
        StringItem item4 = new StringItem("Jonathan", "Geers");
        StringItem item5 = new StringItem("Richard", "Geers");

        SelectorList<StringItem> itemSelectorList = new SelectorList<>();
        itemSelectorList.add(item1);
        itemSelectorList.add(item2);
        itemSelectorList.add(item3);
        itemSelectorList.add(item4);
        itemSelectorList.add(item5);

        searchableNormalInputView.setList(itemSelectorList, "Select Person");
    }

    public class Person implements SelectorInterface {
        String name;
        String surname;

        @Override
        public String getDisplayValue() {
            return name;
        }

        @Override
        public String getDisplayValueLine2() {
            return surname;
        }
    }

}
