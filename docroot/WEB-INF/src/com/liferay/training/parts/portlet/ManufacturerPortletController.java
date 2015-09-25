package com.liferay.training.parts.portlet;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.training.parts.model.Manufacturer;
import com.liferay.training.parts.service.ManufacturerLocalServiceUtil;

import java.util.ArrayList;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

/**
 * Portlet implementation class ManufacturerPortlet
 */
@Controller(value = "manufacturerPortletController")
@RequestMapping("VIEW")
public class ManufacturerPortletController {
	@RenderMapping
	public String handleRenderRequest(RenderRequest request,
			RenderResponse response, Model model) {

		return "view";
	}

	@RenderMapping(params = "view=editManufacturer")
	public String handleEditPartsRenderRequest(RenderRequest request,
			RenderResponse response, Model model) {

		return "edit_manufacturer";
	}

	/**
	 * Adds a new manufacturer to the database
	 * 
	 */
	@ActionMapping(params = "action=addManufacturer")
	public void addManufacturer(ActionRequest request, ActionResponse response)
			throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay) request
				.getAttribute(WebKeys.THEME_DISPLAY);
		long groupId = themeDisplay.getScopeGroupId();

		if (themeDisplay.getPermissionChecker()
				.hasPermission(groupId, "com.liferay.training.parts.model",
						groupId, "ADD_MANUFACTURER")) {

			Manufacturer manufacturer = ManufacturerLocalServiceUtil
					.createManufacturer(0);

			manufacturer.setManufacturerId(ParamUtil.getLong(request,
					"manufacturerId"));
			manufacturer.setName(ParamUtil.getString(request, "name"));
			manufacturer.setEmailAddress(ParamUtil.getString(request,
					"emailAddress"));
			manufacturer.setWebsite(ParamUtil.getString(request, "website"));
			manufacturer.setPhoneNumber(ParamUtil.getString(request,
					"phoneNumber"));
			manufacturer.setCompanyId(themeDisplay.getCompanyId());
			manufacturer.setGroupId(themeDisplay.getScopeGroupId());
			manufacturer.setUserId(themeDisplay.getUserId());

			List<String> errors = new ArrayList<String>();

			if (ManufacturerValidator
					.validateManufacturer(manufacturer, errors)) {

				long userId = themeDisplay.getUserId();

				ManufacturerLocalServiceUtil.addManufacturer(manufacturer,
						userId);

				SessionMessages.add(request, "manufacturer-added");

				response.sendRedirect(request.toString(), response.toString());
			} else {
				for (String error : errors) {
					SessionErrors.add(request, error);
				}

				PortalUtil.copyRequestParameters(request, response);

				response.setRenderParameter("view", "editManufacturer");

			}
		} else {
			SessionErrors.add(request, "permission-error");
			response.sendRedirect(request.toString(), response.toString());

		}
	}

	/**
	 * Updates the database record of an existing manufacturer.
	 * 
	 */
	@ActionMapping(params = "action=updateManufacturer")
	public void updateManufacturer(ActionRequest request,
			ActionResponse response) throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay) request
				.getAttribute(WebKeys.THEME_DISPLAY);
		long groupId = themeDisplay.getScopeGroupId();

		long manufacturerId = ParamUtil.getLong(request, "manufacturerId");
		Manufacturer manufacturer = null;

		try {
			manufacturer = ManufacturerLocalServiceUtil
					.fetchManufacturer(manufacturerId);
		} catch (SystemException se) {
			_log.error(se);
			return;
		}

		if (themeDisplay.getPermissionChecker().hasPermission(groupId,
				"com.liferay.training.parts.model.Manufacturer",
				manufacturer.getManufacturerId(), "UPDATE")) {

			manufacturer.setName(ParamUtil.getString(request, "name"));
			manufacturer.setEmailAddress(ParamUtil.getString(request,
					"emailAddress"));
			manufacturer.setWebsite(ParamUtil.getString(request, "website"));
			manufacturer.setPhoneNumber(ParamUtil.getString(request,
					"phoneNumber"));

			List<String> errors = new ArrayList<String>();

			if (ManufacturerValidator
					.validateManufacturer(manufacturer, errors)) {

				ManufacturerLocalServiceUtil.updateManufacturer(manufacturer);

				SessionMessages.add(request, "manufacturer-updated");

				response.sendRedirect(request.toString(), response.toString());
			} else {
				for (String error : errors) {
					SessionErrors.add(request, error);
				}

				PortalUtil.copyRequestParameters(request, response);

				response.setRenderParameter("view", "editManufacturer");
			}
		} else {
			SessionErrors.add(request, "permission-error");
			response.sendRedirect(request.toString(), response.toString());
		}
	}

	/**
	 * Deletes a manufacturer from the database.
	 * 
	 */
	@ActionMapping(params = "action=deleteManufacturer")
	public void deleteManufacturer(ActionRequest request,
			ActionResponse response) throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay) request
				.getAttribute(WebKeys.THEME_DISPLAY);
		long groupId = themeDisplay.getScopeGroupId();

		long manufacturerId = ParamUtil.getLong(request, "manufacturerId");

		if (themeDisplay.getPermissionChecker().hasPermission(groupId,
				"com.liferay.training.parts.model.Manufacturer",
				manufacturerId, "DELETE")) {

			if (Validator.isNotNull(manufacturerId)) {
				ManufacturerLocalServiceUtil.deleteManufacturer(manufacturerId);

				SessionMessages.add(request, "manufacturer-deleted");

				response.sendRedirect(request.toString(), response.toString());
			} else {
				SessionErrors.add(request, "error-deleting");
			}
		} else {
			SessionErrors.add(request, "permission-error");
			response.sendRedirect(request.toString(), response.toString());
		}
	}

	private static Log _log = LogFactoryUtil
			.getLog(ManufacturerPortletController.class);
}
