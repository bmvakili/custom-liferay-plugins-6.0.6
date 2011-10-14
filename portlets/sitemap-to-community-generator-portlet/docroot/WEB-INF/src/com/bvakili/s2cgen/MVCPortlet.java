package com.bvakili.s2cgen;

import java.io.IOException;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import com.liferay.portal.model.Layout;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.WebKeys;

public class MVCPortlet extends com.liferay.util.bridges.mvc.MVCPortlet {

	public void doView(RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {
			String sitemap = renderRequest.getParameter("sitemap");
			ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
				WebKeys.THEME_DISPLAY);
			createPagesFromSitemap(sitemap, themeDisplay);
			include(viewJSP, renderRequest, renderResponse);
		}
		private void createPagesFromSitemap(String sitemap, ThemeDisplay themeDisplay) {
			try {
			if (sitemap == null) {
				return;
			}
			if (sitemap.trim() == "") {
				return;
			}
			if (_log.isDebugEnabled()) {
				_log.debug(sitemap);
			}
			sitemap = sitemap.trim();
			String[] pages = sitemap.split("\\r?\\n");
			int prevLevel = 0;
			int level = 0;
			Layout curLayout = null;
			Layout rootLayout = null;
			Layout prevLayout = null;
			for (String page : pages) {
				page = page.trim();
				level = determineLevel(page);
				page=page.substring(level + 1).trim();
				if (_log.isDebugEnabled()) {
					_log.debug(page + " " + level + " " + prevLevel);
				}
				if (level == -1) {
					return;
				}
				if (level > prevLevel + 1) {
					return;
				}
				if (rootLayout == null) {
					if (level != 0) {
						return;
					}
					rootLayout = addPage(page, 0, themeDisplay);
					curLayout = rootLayout;
				} else if (prevLayout == null) {
					long layoutId = 0;
					if (level > prevLevel) {
						layoutId = curLayout.getLayoutId();
					}
					curLayout = addPage(page, layoutId, themeDisplay);
					prevLayout = curLayout;
				} else {
					long layoutId = 0;
					if (prevLevel == level) {
						layoutId = curLayout.getParentLayoutId();
					} else if (prevLevel < level) {
						layoutId = curLayout.getLayoutId();
					} else if (prevLevel > level) {
						int diff = prevLevel - level;
						java.util.List<Layout> ancestors = curLayout.getAncestors();
						int size = ancestors.size();
						if (_log.isDebugEnabled()) {
							for (Layout anc : ancestors) {
								_log.debug("\t" + anc.getLayoutId());
							}
						}
						if (size < diff + 1) {
							layoutId = 0;
						} else {
							layoutId = ((Layout)curLayout.getAncestors().get((size - prevLevel) + (diff))).getLayoutId();
						}
					}
					curLayout = addPage(page, layoutId, themeDisplay);
					prevLayout = curLayout;
				}
				prevLevel = level;
			}
			}catch(com.liferay.portal.kernel.exception.PortalException e) {
				e.printStackTrace();
			}catch(com.liferay.portal.kernel.exception.SystemException e) {
				e.printStackTrace();
			}
		}

	private Layout addPage(String page, long parentLayoutId, ThemeDisplay themeDisplay) throws com.liferay.portal.kernel.exception.PortalException, com.liferay.portal.kernel.exception.SystemException {
		long userId = themeDisplay.getUserId();
		long scopeGroupId = themeDisplay.getScopeGroupId();
		String friendlyUrl = page.replaceAll("[^a-zA-Z0-9]","_").trim().toLowerCase();
		ServiceContext serviceContext = new ServiceContext();
		if (_log.isDebugEnabled()) {
			_log.debug("adding : " + page + " to " + parentLayoutId);
		}
		Layout layout = com.liferay.portal.service.LayoutLocalServiceUtil.addLayout(
			userId, //userid
			scopeGroupId, //groupId
			false, //privateLayout
			parentLayoutId, //parentLayoutId
			page, //name
			page, //title
			page, //description
			"portlet", //type
			false, //hidden
			null, //friendlyUrl
			serviceContext //service context
		);
		if (_log.isDebugEnabled()) {
			_log.debug("added : " + layout.getLayoutId() + " with parent " + layout.getParentLayoutId());
		}
		return layout;
	}

	private int determineLevel(String page) {
		int level = -1;
		for (char c : page.toCharArray()) {
			if ('*' == c) {
				level++;
			} else {
				break;
			}
		}
		return level;
	}
	private static Log _log = LogFactoryUtil.getLog(MVCPortlet.class);
}
