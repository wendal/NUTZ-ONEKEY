package club.zhcs.thunder.ext.shiro.aop;

import java.lang.annotation.Annotation;

import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.aop.RoleAnnotationHandler;
import org.apache.shiro.subject.Subject;
import org.nutz.lang.ContinueLoop;
import org.nutz.lang.Each;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.Lang;
import org.nutz.lang.LoopException;

import club.zhcs.thunder.ext.shiro.anno.ThunderRequiresRoles;
import club.zhcs.thunder.vo.InstallPermission;
import club.zhcs.thunder.vo.InstalledRole;

/**
 * 
 * @author 王贵源
 *
 * @email kerbores@kerbores.com
 *
 * @description
 * 
 * @copyright copyright©2016 zhcs.club
 *
 * @createTime 2016年8月23日 下午1:13:25
 */
public class ThunderRoleAnnotationHandler extends RoleAnnotationHandler {

	public ThunderRoleAnnotationHandler() {
		setAnnotationClass(ThunderRequiresRoles.class);
	}

	@Override
	public void assertAuthorized(Annotation a) throws AuthorizationException {
		if (!(a instanceof ThunderRequiresRoles))
			return;

		ThunderRequiresRoles rpAnnotation = (ThunderRequiresRoles) a;
		InstalledRole[] roles_ = rpAnnotation.value();
		Subject subject = getSubject();

		final String[] roles = new String[roles_.length];

		Lang.each(roles_, new Each<InstallPermission>() {

			@Override
			public void invoke(int index, InstallPermission ele, int length) throws ExitLoop, ContinueLoop, LoopException {
				roles[index] = ele.getName();
			}
		});

		if (roles.length == 1) {
			subject.checkRole(roles[0]);
			return;
		}
		if (Logical.AND.equals(rpAnnotation.logical())) {
			getSubject().checkRoles(roles);
			return;
		}
		if (Logical.OR.equals(rpAnnotation.logical())) {
			boolean hasAtLeastOneRoles = false;
			for (String role : roles)
				if (getSubject().hasRole(role))
					hasAtLeastOneRoles = true;
			if (!hasAtLeastOneRoles)
				getSubject().checkRole(roles[0]);
		}
	}
}
