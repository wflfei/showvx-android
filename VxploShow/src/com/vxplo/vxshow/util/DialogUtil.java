package com.vxplo.vxshow.util;

import java.util.Arrays;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.vxplo.vxshow.R;
import com.vxplo.vxshow.app.VxploApplication;
import com.vxplo.vxshow.entity.Idea;
import com.vxplo.vxshow.http.VxHttpCallback;
import com.vxplo.vxshow.http.VxHttpRequest;
import com.vxplo.vxshow.http.VxHttpRequest.VxHttpMethod;
import com.vxplo.vxshow.http.VxHttpTask.ErrorStatus;
import com.vxplo.vxshow.http.constant.Constant;

public class DialogUtil {

	public enum DialogType {
		SEND, SETTING, REPORT
	}

	private static ProgressDialog pDialog;

	public static void showLoadingDialog(Context ctx) {
		if (null == pDialog) {
			pDialog = new ProgressDialog(ctx, ProgressDialog.THEME_HOLO_DARK);
		}
		pDialog.setMessage(VxploApplication.resources
				.getString(R.string.loading_str));
		pDialog.show();
	}

	public static void closeLoadingDialog() {
		if (null != pDialog) {
			pDialog.dismiss();
			pDialog = null;
		}
	}
	
	public static AlertDialog showAlert(final Context context, final String msg, final String title) {
		if (context instanceof Activity && ((Activity) context).isFinishing()) {
			return null;
		}

		final Builder builder = new AlertDialog.Builder(context);
		builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle(title);
		builder.setMessage(msg);
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(final DialogInterface dialog, final int which) {
				dialog.cancel();
			}
		});
		final AlertDialog alert = builder.create();
		alert.show();
		return alert;
	}
	
	public static AlertDialog showAlert(final Context context, final int msg, final int title) {
		if (context instanceof Activity && ((Activity) context).isFinishing()) {
			return null;
		}

		final Builder builder = new AlertDialog.Builder(context);
		builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle(title);
		builder.setMessage(msg);
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(final DialogInterface dialog, final int which) {
				dialog.cancel();
			}
		});
		final AlertDialog alert = builder.create();
		alert.show();
		return alert;
	}
	
	public static void showAlertDialog(Context context, String title, String message) {
		
	}
	
	
	public static void showToolbarDialog(Context ctx, DialogType type, Idea idea) {
		AlertDialog dialog = new AlertDialog.Builder(ctx).create();
		View view = generateView(ctx, type, dialog, idea);
		dialog.setView(view);
		dialog.show();
	}

	private static View generateView(Context ctx, DialogType type,
			AlertDialog dialog, Idea idea) {
		// TODO Auto-generated method stub
		LayoutInflater inflater = LayoutInflater.from(ctx);
		View v = null;
		switch (type) {
		case SEND:
			v = createSendView(inflater, dialog, idea);
			break;
		case SETTING:
			v = createSettingView(inflater, dialog, idea);
			break;
		case REPORT:
			v = createReportView(inflater, dialog, idea);
			break;
		}
		return v;
	}

	private static View createReportView(LayoutInflater inflater,
			final AlertDialog dialog, final Idea idea) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.operation_dialog_report, null);
		TextView title = (TextView) v.findViewById(R.id.title);
		final TextView edit = (TextView) v.findViewById(R.id.edit_report);
		Button report = (Button) v.findViewById(R.id.btn_report);
		ImageButton close = (ImageButton) v.findViewById(R.id.close);

		String reportTitle = VxploApplication.resources
				.getString(R.string.title_report);

		title.setText(reportTitle);
		title.setTextColor(Color.WHITE);
		title.setTextSize(20);

		close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});

		final Spinner reportSpinner = (Spinner) v
				.findViewById(R.id.report_spinner);
		final String[] reportStrs = VxploApplication.resources
				.getStringArray(R.array.report);
		ArrayAdapter<String> reportAdapter = new ArrayAdapter<String>(
				dialog.getContext(), R.layout.layout_spinner, reportStrs);
		reportAdapter.setDropDownViewResource(R.layout.layout_spinner_dropdown);
		reportSpinner.setAdapter(reportAdapter);
		
		reportSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				if(arg2==4) {
					edit.setFocusable(true);
					edit.setFocusableInTouchMode(true);
					edit.setText("");
				} else {
					edit.setFocusable(false);
					edit.setText(reportStrs[arg2]);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		report.setText(reportTitle);

		report.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				VxHttpRequest request = new VxHttpRequest(
						dialog.getContext(),
						VxHttpMethod.POST,
						Constant.getIdeaReportUrl(),
						new String[] { "nid", "reason" },
						new String[] {
								idea.getNid(),
								edit.getText().toString().trim() });
				request.setCallback(new VxHttpCallback() {

					@Override
					public void beforeSend() {
						// TODO Auto-generated method stub
						showLoadingDialog(dialog.getContext());
					}

					@Override
					public void success(String result) throws Exception {
						// TODO Auto-generated method stub
						try {
							showToast(dialog.getContext(),
									VxploApplication.resources
											.getString(R.string.success));
							dialog.dismiss();
						} catch (Exception e) {
							throw new Exception(result);
						}
					}

					@Override
					public void error(ErrorStatus status, Exception... exs) {
						// TODO Auto-generated method stub
						if(exs.length>0) {
							showToast(dialog.getContext(), exs[0].getMessage());
						} else {
							showToast(dialog.getContext(), status.name());
						}
					}

					@Override
					public void complete() {
						// TODO Auto-generated method stub
						closeLoadingDialog();
					}

				});
				request.send();
			}
		});
		
		return v;
	}

	private static View createSettingView(LayoutInflater inflater,
			final AlertDialog dialog, final Idea idea) {
		View v = inflater.inflate(R.layout.operation_dialog_setting, null);
		TextView title = (TextView) v.findViewById(R.id.title);
		final EditText name = (EditText) v.findViewById(R.id.edit_pname);
		Button setting = (Button) v.findViewById(R.id.btn_setting);
		ImageButton close = (ImageButton) v.findViewById(R.id.close);

		String settingTitle = VxploApplication.resources
				.getString(R.string.title_setting);

		title.setText(settingTitle);
		title.setTextColor(Color.WHITE);
		title.setTextSize(20);

		name.setText(idea.getTitle());

		close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});

		final Spinner category = (Spinner) v.findViewById(R.id.category);
		ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(
				dialog.getContext(), R.layout.layout_spinner,
				idea.getOptValues());
		categoryAdapter
				.setDropDownViewResource(R.layout.layout_spinner_dropdown);
		category.setAdapter(categoryAdapter);
		category.setSelection(
				Arrays.binarySearch(idea.getOptValues(),
						idea.getCurrentCategory()), true);

		final Spinner privacy = (Spinner) v.findViewById(R.id.privacy);
		final String[] privacyStrs;
		String[] tempStrs = VxploApplication.resources
				.getStringArray(R.array.privacy);
		if (!idea.isCanSetToPrivate()) {
			privacyStrs = Arrays.copyOfRange(tempStrs, 1, tempStrs.length);
		} else {
			privacyStrs = tempStrs;
		}
		ArrayAdapter<String> privacyAdapter = new ArrayAdapter<String>(
				dialog.getContext(), R.layout.layout_spinner, privacyStrs);
		privacyAdapter
				.setDropDownViewResource(R.layout.layout_spinner_dropdown);
		privacy.setAdapter(privacyAdapter);
		privacy.setSelection(
				Arrays.binarySearch(privacyStrs, idea.getCurrentPriviledge()),
				true);

		setting.setText(settingTitle);

		setting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String nameStr = name.getText().toString().trim();

				if (StringUtil.isStringEmptyOrNull(nameStr)) {
					showToast(dialog.getContext(), VxploApplication.resources
							.getString(R.string.alert_empty));
					return;
				}
				VxHttpRequest request = new VxHttpRequest(
						dialog.getContext(),
						VxHttpMethod.POST,
						Constant.getIdeaSettingUrl(),
						new String[] { "nid", "title", "category", "priviledge" },
						new String[] {
								idea.getNid(),
								nameStr,
								idea.getOpts()
										.get(category.getSelectedItemPosition())
										.getoId(),
								privacyStrs[privacy.getSelectedItemPosition()] });
				request.setCallback(new VxHttpCallback() {

					@Override
					public void beforeSend() {
						// TODO Auto-generated method stub
						showLoadingDialog(dialog.getContext());
					}

					@Override
					public void success(String result) throws Exception {
						// TODO Auto-generated method stub
						try {
							showToast(dialog.getContext(),
									VxploApplication.resources
											.getString(R.string.success));
							dialog.dismiss();
						} catch (Exception e) {
							throw new Exception(result);
						}
					}

					@Override
					public void error(ErrorStatus status, Exception... exs) {
						// TODO Auto-generated method stub
						if(exs.length>0) {
							showToast(dialog.getContext(), exs[0].getMessage());
						} else {
							showToast(dialog.getContext(), status.name());
						}
					}

					@Override
					public void complete() {
						// TODO Auto-generated method stub
						closeLoadingDialog();
					}

				});
				request.send();
			}
		});

		return v;
	}

	private static View createSendView(LayoutInflater inflater,
			final AlertDialog dialog, final Idea idea) {
		View v = inflater.inflate(R.layout.operation_dialog_send, null);
		TextView title = (TextView) v.findViewById(R.id.title);
		final EditText name = (EditText) v.findViewById(R.id.edit_umail);
		final EditText pass = (EditText) v.findViewById(R.id.edit_upass);
		Button send = (Button) v.findViewById(R.id.btn_send);
		ImageButton close = (ImageButton) v.findViewById(R.id.close);

		String sendTitle = VxploApplication.resources
				.getString(R.string.title_send);

		title.setText(sendTitle);
		title.setTextColor(Color.WHITE);
		title.setTextSize(20);

		close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});

		send.setText(sendTitle);
		send.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String nameStr = name.getText().toString().trim();
				String passStr = pass.getText().toString().trim();
				if (StringUtil.isStringEmptyOrNull(nameStr)
						|| StringUtil.isStringEmptyOrNull(passStr)) {
					showToast(dialog.getContext(), VxploApplication.resources
							.getString(R.string.alert_empty));
					return;
				}
				VxHttpRequest request = new VxHttpRequest(dialog.getContext(),
						VxHttpMethod.POST, Constant.getIdeaSendUrl(),
						new String[] { "email", "pass", "nid" }, new String[] {
								nameStr, passStr, idea.getNid() });
				request.setCallback(new VxHttpCallback() {

					@Override
					public void beforeSend() {
						// TODO Auto-generated method stub
						showLoadingDialog(dialog.getContext());
					}

					@Override
					public void success(String result) throws Exception {
						// TODO Auto-generated method stub
						try {
							showToast(dialog.getContext(),
									VxploApplication.resources
											.getString(R.string.success));
							dialog.dismiss();
						} catch (Exception e) {
							throw new Exception(result);
						}
					}

					@Override
					public void error(ErrorStatus status, Exception... exs) {
						// TODO Auto-generated method stub
						if(exs.length>0) {
							showToast(dialog.getContext(), exs[0].getMessage());
						} else {
							showToast(dialog.getContext(), status.name());
						}
					}

					@Override
					public void complete() {
						// TODO Auto-generated method stub
						closeLoadingDialog();
					}

				});
				request.send();
			}
		});
		return v;
	}

	public static void showToast(Context ctx, String msg) {
		Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
	}

}
