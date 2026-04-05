package com.example.practico1;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.practico1.databinding.ActivityMainBinding;
import com.example.practico1.viewModel.ConversionDirection;
import com.example.practico1.viewModel.CurrencyViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private CurrencyViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setIcon(R.mipmap.ic_launcher);
            setTitle(R.string.title_main);
        }

        viewModel = new ViewModelProvider(
                this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())
        ).get(CurrencyViewModel.class);

        setupInputSync();
        setupDirectionRadios();
        setupActions();
        observeViewModel();
        applyDirectionUi();
    }

    private boolean isConvertingToEuros() {
        return binding.radioToEur.isChecked();
    }

    private void setupInputSync() {
        binding.editDollars.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isConvertingToEuros()) {
                    viewModel.setInputAmount(s != null ? s.toString() : "");
                    clearFieldErrorIfNeeded();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        binding.editEuros.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!isConvertingToEuros()) {
                    viewModel.setInputAmount(s != null ? s.toString() : "");
                    clearFieldErrorIfNeeded();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void clearFieldErrorIfNeeded() {
        if (binding.editDollars.getError() != null) {
            binding.editDollars.setError(null);
        }
        if (binding.editEuros.getError() != null) {
            binding.editEuros.setError(null);
        }
    }

    private void setupDirectionRadios() {
        binding.radioGroupDirection.setOnCheckedChangeListener((RadioGroup group, int checkedId) -> {
            if (checkedId == R.id.radio_to_eur) {
                viewModel.setDirection(ConversionDirection.TO_EUR);
            } else if (checkedId == R.id.radio_to_usd) {
                viewModel.setDirection(ConversionDirection.TO_USD);
            }
            applyDirectionUi();
        });
    }

    private void applyDirectionUi() {
        boolean toEur = isConvertingToEuros();
        binding.editDollars.setFocusable(toEur);
        binding.editDollars.setFocusableInTouchMode(toEur);
        binding.editDollars.setClickable(toEur);
        binding.editDollars.setCursorVisible(toEur);

        binding.editEuros.setFocusable(!toEur);
        binding.editEuros.setFocusableInTouchMode(!toEur);
        binding.editEuros.setClickable(!toEur);
        binding.editEuros.setCursorVisible(!toEur);

        binding.editDollars.setText("");
        binding.editEuros.setText("");
        viewModel.setInputAmount("");
    }

    private void setupActions() {
        binding.buttonConvert.setOnClickListener(v -> {
            if (isConvertingToEuros()) {
                viewModel.setInputAmount(binding.editDollars.getText() != null
                        ? binding.editDollars.getText().toString()
                        : "");
            } else {
                viewModel.setInputAmount(binding.editEuros.getText() != null
                        ? binding.editEuros.getText().toString()
                        : "");
            }
            viewModel.convert();
        });

        binding.buttonChangeRate.setOnClickListener(v -> showChangeRateDialog());
    }

    private void observeViewModel() {
        viewModel.getRateDisplay().observe(this, text -> {
            if (text != null) {
                binding.textRate.setText(text);
            }
        });

        viewModel.getResultAmount().observe(this, text -> {
            String t = text != null ? text : "";
            if (isConvertingToEuros()) {
                binding.editEuros.setText(t);
            } else {
                binding.editDollars.setText(t);
            }
        });

        viewModel.getConversionErrorResId().observe(this, this::applyConversionError);

        viewModel.getRateErrorResId().observe(this, this::applyRateError);
    }

    private void applyConversionError(@Nullable @StringRes Integer messageResId) {
        binding.editDollars.setError(null);
        binding.editEuros.setError(null);
        if (messageResId == null) {
            return;
        }
        String msg = getString(messageResId);
        if (isConvertingToEuros()) {
            binding.editDollars.setError(msg);
        } else {
            binding.editEuros.setError(msg);
        }
    }

    private void applyRateError(@Nullable @StringRes Integer messageResId) {
        if (messageResId == null) {
            return;
        }
        Snackbar.make(binding.getRoot(), getString(messageResId), Snackbar.LENGTH_LONG).show();
    }

    private void showChangeRateDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_change_rate, null);
        EditText editRate = dialogView.findViewById(R.id.edit_new_rate);

        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.dialog_change_rate_title)
                .setMessage(R.string.dialog_change_rate_message)
                .setView(dialogView)
                .setPositiveButton(R.string.action_ok, (d, which) -> {
                    CharSequence text = editRate.getText();
                    viewModel.updateExchangeRateFromText(text != null ? text.toString() : "");
                })
                .setNegativeButton(R.string.action_cancel, null)
                .show();
    }
}
