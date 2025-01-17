package com.ruoyi.qixing.controller;

import cn.hutool.core.util.ObjUtil;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.system.service.ISysMenuService;
import io.github.wujun728.db.record.Db;
import io.github.wujun728.db.record.Record;
import io.github.wujun728.db.utils.RecordUtil;
import io.github.wujun728.db.utils.TreeBuildUtil;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author wujun
 */
@RestController
@RequestMapping("/qixing")
public class QixingController extends BaseController
{

    @Autowired
    private ISysMenuService menuService;

    //@PreAuthorize("@ss.hasPermi('tool:gen:query')")
    @GetMapping(value = "/test")
    public AjaxResult getInfo()
    {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("info", "sucess");
        map.put("rows", Lists.newArrayList());
        return success(map);
    }


    @GetMapping("getRouters")
    public AjaxResult getRouters() throws IOException {
        Long userId = SecurityUtils.getUserId();
        //List<SysMenu> menus = menuService.selectMenuTreeByUserId(userId);
        /*select distinct m.menu_id, m.parent_id, m.menu_name, m.path, m.component, m.`query`, m.visible, m.status, ifnull(m.perms,'') as perms, m.is_frame, m.is_cache, m.menu_type, m.icon, m.order_num, m.create_time
        from sys_menu m
        left join sys_role_menu rm on m.menu_id = rm.menu_id
        left join sys_user_role ur on rm.role_id = ur.role_id
        left join sys_role ro on ur.role_id = ro.role_id
        left join sys_user u on ur.user_id = u.user_id
        where u.user_id = #{userId} and m.menu_type in ('M', 'C') and m.status = 0  AND ro.status = 0
        order by m.parent_id, m.order_num*/
        //ActiveRecordUtil.initActiveRecordPlugin("main", SpringUtils.getBean(DataSource.class));
        Db.init("main", SpringUtils.getBean(DataSource.class));
        List<Record> apps = Db.use().find(" SELECT * from sys_menu where parent_id = 0 ");
        List apps1 = RecordUtil.recordToMaps(apps,true);
        List<Record> menus = Db.use().find(" SELECT * from sys_menu where menu_type != 'F' ORDER BY parent_id,order_num ");
        List<Map> menus1 = RecordUtil.recordToMaps(menus,true);
        for(Map map : menus1){
            if(ObjUtil.isNotEmpty(map.get("component"))){
                map.put("component",map.get("component")+".html");
            }
        }
        menus1 = (List) menus1.stream().map(item->{return item;}).collect(Collectors.toList());
        List menus2 = TreeBuildUtil.listToTree(menus1,"0","menu_id","parent_id");
        Map data = new HashMap<>();
        data.put("apps",apps1);
        data.put("menus",menus2);
        return AjaxResult.success(data);
    }

}