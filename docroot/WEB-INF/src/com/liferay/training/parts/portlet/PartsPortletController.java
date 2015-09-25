package com.liferay.training.parts.portlet;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.training.parts.model.Part;
import com.liferay.training.parts.service.PartLocalServiceUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
 * Portlet implementation class PartsPortlet
 */
@Controller(value = "partsPortletController")
@RequestMapping("VIEW")
public class PartsPortletController {

	@RenderMapping
	public String handleRenderRequest(RenderRequest request,
			RenderResponse response, Model model) {

		return "view";
	}

	@RenderMapping(params = "view=editPart")
	public String handleEditPartsRenderRequest(RenderRequest request,
			RenderResponse response) {

		return "edit_part";

	}

	@ActionMapping(params = "action=addPart")
	public void addPart(ActionRequest request, ActionResponse response) {

		ThemeDisplay themeDisplay = (ThemeDisplay) request
				.getAttribute(WebKeys.THEME_DISPLAY);
		long groupId = themeDisplay.getScopeGroupId();

		if (themeDisplay.getPermissionChecker().hasPermission(groupId,
				"com.liferay.training.parts.model", groupId, "ADD_PART")) {
			Part part = PartLocalServiceUtil.createPart(0);

			part.setPartId(ParamUtil.getLong(request, "partId"));

			Map<Locale, String> nameMap = LocalizationUtil.getLocalizationMap(
					request, "name");
			part.setNameMap(nameMap);

			part.setPartNumber(ParamUtil.getString(request, "partNumber"));

			int orderDateMonth = ParamUtil
					.getInteger(request, "orderDateMonth");
			int orderDateDay = ParamUtil.getInteger(request, "orderDateDay");
			int orderDateYear = ParamUtil.getInteger(request, "orderDateYear");
			Date orderDate = PortalUtil.getDate(orderDateMonth, orderDateDay,
					orderDateYear);
			part.setOrderDate(orderDate);

			part.setQuantity(ParamUtil.getInteger(request, "quantity"));
			part.setManufacturerId(ParamUtil.getLong(request, "manufacturerId"));
			part.setCompanyId(themeDisplay.getCompanyId());
			part.setGroupId(themeDisplay.getScopeGroupId());
			part.setUserId(themeDisplay.getUserId());

			List<String> errors = new ArrayList<String>();

			if (PartValidator.validatePart(part, errors)) {
				long userId = themeDisplay.getUserId();

				try {
					PartLocalServiceUtil.addPart(part, userId);
				} catch (Exception e) {

					_log.error("Failed to add part due to exception ", e);
				}

				SessionMessages.add(request, "part-added");
				try {
					response.sendRedirect(request.toString(),
							response.toString());
				} catch (IOException e) {

					_log.error("Failed to redirect to part-added view!");
				}

			} else {
				for (String error : errors) {
					SessionErrors.add(request, error);
				}

				PortalUtil.copyRequestParameters(request, response);
				response.setRenderParameter("view", "editPart");

			}
		} else {
			SessionErrors.add(request, "permission-error");

			try {
				response.sendRedirect(request.toString(), response.toString());
			} catch (IOException e) {

				_log.error("Failed to redirect to permission-error view");
			}
		}

	}

	/**
	 * Deletes a part from the database.
	 * 
	 */
	@ActionMapping(params = "action=deletePart")
	public void deletePart(ActionRequest request, ActionResponse response) {

		ThemeDisplay themeDisplay = (ThemeDisplay) request
				.getAttribute(WebKeys.THEME_DISPLAY);
		long groupId = themeDisplay.getScopeGroupId();

		long partId = ParamUtil.getLong(request, "partId");

		if (themeDisplay.getPermissionChecker().hasPermission(groupId,
				"com.liferay.training.parts.model.Part", partId, "DELETE")) {

			if (Validator.isNotNull(partId)) {
				try {
					PartLocalServiceUtil.deletePart(partId);
				} catch (Exception e) {
					_log.error("Failed to delete a part due to exception ", e);
				}

				SessionMessages.add(request, "part-deleted");

				try {
					response.sendRedirect(request.toString(),
							response.toString());
				} catch (IOException e) {

					_log.error("Failed to redirect after deleting part ", e);
				}
			} else {
				SessionErrors.add(request, "deletion-error");
			}
		} else {
			SessionErrors.add(request, "permission-error");
			try {
				response.sendRedirect(request.toString(), response.toString());
			} catch (IOException e) {

				_log.error("Failed to redirect after deleting part ", e);
			}
		}
	}

	/**
	 * Updates the database record of an existing part.
	 * 
	 * @return
	 * @return
	 * 
	 */

	@ActionMapping(params = "action=updatePart")
	public void updatePart(ActionRequest request, ActionResponse response)
			throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay) request
				.getAttribute(WebKeys.THEME_DISPLAY);
		long groupId = themeDisplay.getScopeGroupId();

		long partId = ParamUtil.getLong(request, "partId");
		Part part = null;

		try {
			part = PartLocalServiceUtil.fetchPart(partId);
		} catch (SystemException se) {
			_log.error(se);
			return;
		}

		if (themeDisplay.getPermissionChecker().hasPermission(groupId,
				"com.liferay.training.parts.model.Part", part.getPartId(),
				"UPDATE")) {

			Map<Locale, String> nameMap = LocalizationUtil.getLocalizationMap(
					request, "name");
			part.setNameMap(nameMap);

			part.setPartNumber(ParamUtil.getString(request, "partNumber"));

			int orderDateMonth = ParamUtil
					.getInteger(request, "orderDateMonth");
			int orderDateDay = ParamUtil.getInteger(request, "orderDateDay");
			int orderDateYear = ParamUtil.getInteger(request, "orderDateYear");
			Date orderDate = PortalUtil.getDate(orderDateMonth, orderDateDay,
					orderDateYear);
			part.setOrderDate(orderDate);

			part.setQuantity(ParamUtil.getInteger(request, "quantity"));
			part.setManufacturerId(ParamUtil.getLong(request, "manufacturerId"));

			List<String> errors = new ArrayList<String>();

			if (PartValidator.validatePart(part, errors)) {

				PartLocalServiceUtil.updatePart(part);

				SessionMessages.add(request, "part-updated");

				response.sendRedirect(request.toString(), response.toString());
			} else {
				for (String error : errors) {
					SessionErrors.add(request, error);
				}

				PortalUtil.copyRequestParameters(request, response);

				response.setRenderParameter("view", "editPart");			}
		} else {
			SessionErrors.add(request, "permission-error");
			response.sendRedirect(request.toString(), response.toString());
		}

	}

	private static Log _log = LogFactoryUtil
			.getLog(PartsPortletController.class);

}
