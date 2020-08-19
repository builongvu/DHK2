package vn.vissoft.dashboard.services.impl;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.vissoft.dashboard.config.I18N;
import vn.vissoft.dashboard.dto.GroupServiceDTO;
import vn.vissoft.dashboard.helper.Constants;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.mapper.BaseMapper;
import vn.vissoft.dashboard.model.ActionDetail;
import vn.vissoft.dashboard.model.ConfigRoles;
import vn.vissoft.dashboard.model.ConfigRolesObjects;
import vn.vissoft.dashboard.model.GroupService;
import vn.vissoft.dashboard.repo.ActionDetailRepo;
import vn.vissoft.dashboard.services.ActionDetailService;

@Service
public class ActionDetailServiceImpl implements ActionDetailService {

    private BaseMapper<GroupService, GroupServiceDTO> mapper = new BaseMapper<GroupService, GroupServiceDTO>(GroupService.class, GroupServiceDTO.class);

    public static final Logger LOGGER = LogManager.getLogger(ActionDetailServiceImpl.class);

    @Autowired
    private ActionDetailRepo actionDetailRepo;

    public void addLogService(vn.vissoft.dashboard.model.Service newsService, vn.vissoft.dashboard.model.Service oldService, Long actionAuditId) {

        if (!DataUtil.isNullOrEmpty(newsService.getName())) {
            if (oldService == null) {
                this.createActionDetail(I18N.get(Constants.SERVICES.NAME), newsService.getName(), actionAuditId, null);
            } else {
                if (!newsService.getName().equals(oldService.getName())) {
                    this.createActionDetail(I18N.get(Constants.SERVICES.NAME), newsService.getName(), actionAuditId, oldService.getName());
                }
            }
        }

        if (!DataUtil.isNullOrEmpty(newsService.getCode())) {
            if (oldService == null) {
                this.createActionDetail(I18N.get(Constants.SERVICES.CODE), newsService.getCode(), actionAuditId, null);
            } else {
                if (!newsService.getCode().equals(oldService.getCode())) {
                    this.createActionDetail(I18N.get(Constants.SERVICES.CODE), newsService.getCode(), actionAuditId, oldService.getCode());
                }
            }
        }

        if (!DataUtil.isNullOrEmpty(newsService.getExp())) {
            if (oldService == null) {
                this.createActionDetail(I18N.get(Constants.SERVICES.EXP), newsService.getExp(), actionAuditId, null);
            } else {
                if (!newsService.getExp().equals(oldService.getExp())) {
                    this.createActionDetail(I18N.get(Constants.SERVICES.EXP), newsService.getExp(), actionAuditId, oldService.getExp());
                }
            }
        } else {
            if (oldService != null) {
                if (!DataUtil.isNullOrEmpty(oldService.getExp())) {
                    this.createActionDetail(I18N.get(Constants.SERVICES.EXP), null, actionAuditId, oldService.getExp());
                }
            }
        }

        if (newsService.getParentId() != null) {
            if (oldService == null) {
                this.createActionDetail(I18N.get(Constants.SERVICES.PARENTID), String.valueOf(newsService.getParentId()), actionAuditId, null);
            } else if (!newsService.getParentId().equals(oldService.getParentId())) {
                this.createActionDetail(I18N.get(Constants.SERVICES.PARENTID), String.valueOf(newsService.getParentId()), actionAuditId, String.valueOf(oldService.getParentId()));
            }
        }

        if (!DataUtil.isNullOrEmpty(newsService.getUnitCode())) {
            if (oldService == null) {
                this.createActionDetail(I18N.get(Constants.SERVICES.UNITCODE), newsService.getUnitCode(), actionAuditId, null);
            } else {
                if (!newsService.getUnitCode().equals(oldService.getUnitCode())) {
                    this.createActionDetail(I18N.get(Constants.SERVICES.UNITCODE), newsService.getUnitCode(), actionAuditId, oldService.getUnitCode());
                }
            }
        }

        if (newsService.getFromDate() != null) {
            if (!DataUtil.isNullOrEmpty(newsService.getFromDate().toString())) {
                if (oldService == null) {
                    this.createActionDetail(I18N.get(Constants.SERVICES.FROMDATE), newsService.getFromDate().toString(), actionAuditId, null);
                } else {
                    if (oldService.getFromDate() != null) {
                        if (!newsService.getFromDate().toString().equals(oldService.getFromDate().toString())) {
                            this.createActionDetail(I18N.get(Constants.SERVICES.FROMDATE), newsService.getFromDate().toString(), actionAuditId, oldService.getFromDate().toString());
                        }
                    } else {
                        this.createActionDetail(I18N.get(Constants.SERVICES.FROMDATE), newsService.getFromDate().toString(), actionAuditId, null);
                    }
                }
            }
        }

        if (newsService.getToDate() != null) {
            if (!DataUtil.isNullOrEmpty(newsService.getToDate().toString())) {
                if (oldService == null) {
                    this.createActionDetail(I18N.get(Constants.SERVICES.TODATE), newsService.getToDate().toString(), actionAuditId, null);
                } else {
                    if (oldService.getToDate() != null) {
                        if (!newsService.getToDate().toString().equals(oldService.getToDate().toString())) {
                            this.createActionDetail(I18N.get(Constants.SERVICES.TODATE), newsService.getToDate().toString(), actionAuditId, oldService.getToDate().toString());
                        }
                    } else {
                        this.createActionDetail(I18N.get(Constants.SERVICES.TODATE), newsService.getToDate().toString(), actionAuditId, null);
                    }
                }
            }
        }

        if (!DataUtil.isNullOrEmpty(newsService.getStatus())) {
            if (oldService == null) {
                this.createActionDetail(I18N.get(Constants.SERVICES.STATUS), newsService.getStatus(), actionAuditId, null);
            } else {
                if (!newsService.getStatus().equals(oldService.getStatus())) {
                    this.createActionDetail(I18N.get(Constants.SERVICES.STATUS), newsService.getStatus(), actionAuditId, oldService.getStatus());
                }
            }
        }

        if (!DataUtil.isNullOrEmpty(newsService.getServiceCalcType())) {
            if (oldService == null) {
                this.createActionDetail(I18N.get(Constants.SERVICES.CALCTYPE), newsService.getStatus(), actionAuditId, null);
            } else {
                if (!newsService.getServiceCalcType().equals(oldService.getServiceCalcType())) {
                    this.createActionDetail(I18N.get(Constants.SERVICES.CALCTYPE), newsService.getServiceCalcType(), actionAuditId, oldService.getServiceCalcType());
                }
            }
        }

        if (oldService == null) {
            this.createActionDetail(I18N.get(Constants.SERVICES.GROUPID), newsService.getGroupServiceId().toString(), actionAuditId, null);
            this.createActionDetail(I18N.get(Constants.SERVICES.SERVICECYCLE), String.valueOf(newsService.getServiceCycle()), actionAuditId, null);
            this.createActionDetail(I18N.get(Constants.SERVICES.SERVICETYPE), String.valueOf(newsService.getServiceType()), actionAuditId, null);
            if (!DataUtil.isNullOrZero(newsService.getServiceOrder())) {
                this.createActionDetail(I18N.get(Constants.SERVICES.SERVICEORDER), String.valueOf(newsService.getServiceOrder()), actionAuditId, null);
            }
        } else {
            if (newsService.getAssignType() != (oldService.getAssignType())) {
                this.createActionDetail(I18N.get(Constants.SERVICES.ASSIGNTYPE), String.valueOf(newsService.getAssignType()), actionAuditId, String.valueOf(oldService.getAssignType()));
            }
            if (!newsService.getGroupServiceId().equals(oldService.getGroupServiceId())) {
                this.createActionDetail(I18N.get(Constants.SERVICES.GROUPID), newsService.getGroupServiceId().toString(), actionAuditId, oldService.getGroupServiceId().toString());
            }

            if (newsService.getServiceType() != (oldService.getServiceType())) {
                this.createActionDetail(I18N.get(Constants.SERVICES.SERVICETYPE), String.valueOf(newsService.getServiceType()), actionAuditId, String.valueOf(oldService.getServiceType()));
            }

            if (newsService.getServiceCycle() != (oldService.getServiceCycle())) {
                this.createActionDetail(I18N.get(Constants.SERVICES.SERVICECYCLE), String.valueOf(newsService.getServiceCycle()), actionAuditId, String.valueOf(oldService.getServiceCycle()));
            }

            if (DataUtil.isNullOrZero(oldService.getServiceOrder())) {
                if (!DataUtil.isNullOrZero(newsService.getServiceOrder())) {
                    this.createActionDetail(I18N.get(Constants.SERVICES.SERVICEORDER), String.valueOf(newsService.getServiceOrder()), actionAuditId, null);
                }
            } else if (!DataUtil.isNullOrZero(oldService.getServiceOrder())) {
                if (!DataUtil.isNullOrZero(newsService.getServiceOrder())) {
                    if (!newsService.getServiceOrder().equals(oldService.getServiceOrder())) {
                        this.createActionDetail(I18N.get(Constants.SERVICES.SERVICEORDER), String.valueOf(newsService.getServiceOrder()), actionAuditId, String.valueOf(oldService.getServiceOrder()));
                    }
                } else if (DataUtil.isNullOrZero(newsService.getServiceOrder())) {
                    if (!DataUtil.isNullOrZero(oldService.getServiceOrder())) {
                        this.createActionDetail(I18N.get(Constants.SERVICES.SERVICEORDER), null, actionAuditId, String.valueOf(oldService.getServiceOrder()));
                    }
                }
            }
        }
    }


    @Override
    public void lognew(GroupService groupService, Long actionAuditID) {
        actionDetailRepo.logNew(groupService, actionAuditID);
    }

    @Override
    public void addLogService(GroupService groupService, GroupService oldgroupService, Long actionAuditId) {
        if (!DataUtil.isNullOrEmpty(groupService.getName())) {
            if (oldgroupService == null) {
                this.createActionDetail(I18N.get(Constants.GROUPSERVICES.NAME), groupService.getName(), actionAuditId, null);
            } else {
                if (!groupService.getName().equals(groupService.getName())) {
                    this.createActionDetail(I18N.get(Constants.GROUPSERVICES.NAME), groupService.getName(), actionAuditId, oldgroupService.getStatus());
                }
            }
        }
        if (!DataUtil.isNullOrEmpty(groupService.getStatus())) {
            if (oldgroupService == null) {
                this.createActionDetail(I18N.get(Constants.GROUPSERVICES.STATUS), groupService.getStatus(), actionAuditId, null);
            } else {
                if (!groupService.getStatus().equals(groupService.getStatus())) {
                    this.createActionDetail(I18N.get(Constants.GROUPSERVICES.STATUS), groupService.getStatus(), actionAuditId, oldgroupService.getStatus());
                }
            }
        }
        if (groupService.getProductId() != null) {
            if (!DataUtil.isNullOrEmpty(groupService.getProductId().toString())) {
                this.createActionDetail(I18N.get(Constants.GROUPSERVICES.PRODUCT_ID), groupService.getProductId().toString(), actionAuditId, null);
            } else {
                if (!groupService.getProductId().equals(groupService.getProductId())) {
                    if (oldgroupService != null) {
                        this.createActionDetail(I18N.get(Constants.GROUPSERVICES.PRODUCT_ID), groupService.getProductId().toString(), actionAuditId, oldgroupService.getProductId().toString());
                    }

                }
            }
        }

    }

    @Override
    public ActionDetail createActionDetail(String name, String newValue, Long actionAuditId, String oldValue) {
        try {
            ActionDetail actionDetail = new ActionDetail();
            actionDetail.setActionAuditId(DataUtil.safeToLong(actionAuditId));
            actionDetail.setColumnName(DataUtil.safeToString(name));
            actionDetail.setNewValue(newValue);
            actionDetail.setOldValue(oldValue);
            return actionDetailRepo.save(actionDetail);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void addLogObjectRole(ConfigRolesObjects newConfigRolesObjects, ConfigRolesObjects oldConfigRolesObjects, Long actionAuditId) {
        try {
            if (oldConfigRolesObjects == null) {
                this.createActionDetail(I18N.get(Constants.CONFIG_OBJECT_ROLE.OBJECTID), String.valueOf(newConfigRolesObjects.getObjectId()), actionAuditId, null);
                this.createActionDetail(I18N.get(Constants.CONFIG_OBJECT_ROLE.ACTION), newConfigRolesObjects.getAction(), actionAuditId, null);
                this.createActionDetail(I18N.get(Constants.CONFIG_OBJECT_ROLE.ISDEFAULT), newConfigRolesObjects.getIsDefault().toString(), actionAuditId, null);
                this.createActionDetail(I18N.get(Constants.CONFIG_OBJECT_ROLE.ROLEID), newConfigRolesObjects.getRoleId().toString(), actionAuditId, null);
                this.createActionDetail(I18N.get(Constants.CONFIG_OBJECT_ROLE.STATUS), newConfigRolesObjects.getStatus().toString(), actionAuditId, null);
            } else {
                if (!newConfigRolesObjects.getObjectId().equals(oldConfigRolesObjects.getObjectId())) {
                    this.createActionDetail(I18N.get(Constants.CONFIG_OBJECT_ROLE.OBJECTID), newConfigRolesObjects.getObjectId().toString(), actionAuditId, oldConfigRolesObjects.getObjectId().toString());
                }

                if (DataUtil.isNullOrEmpty(newConfigRolesObjects.getAction())) {
                    this.createActionDetail(I18N.get(Constants.CONFIG_OBJECT_ROLE.ACTION), null, actionAuditId, oldConfigRolesObjects.getAction());
                } else if (DataUtil.isNullOrEmpty(oldConfigRolesObjects.getAction())) {
                    this.createActionDetail(I18N.get(Constants.CONFIG_OBJECT_ROLE.ACTION), newConfigRolesObjects.getAction(), actionAuditId, null);
                } else
                   if (!newConfigRolesObjects.getAction().equals(oldConfigRolesObjects.getAction())) {
                    this.createActionDetail(I18N.get(Constants.CONFIG_OBJECT_ROLE.ACTION), newConfigRolesObjects.getAction(), actionAuditId, oldConfigRolesObjects.getAction());
                }

                if (!newConfigRolesObjects.getIsDefault().equals(oldConfigRolesObjects.getIsDefault())) {
                    this.createActionDetail(I18N.get(Constants.CONFIG_OBJECT_ROLE.ISDEFAULT), newConfigRolesObjects.getIsDefault().toString(), actionAuditId, oldConfigRolesObjects.getIsDefault().toString());
                }

                if (!newConfigRolesObjects.getRoleId().equals(oldConfigRolesObjects.getRoleId())) {
                    this.createActionDetail(I18N.get(Constants.CONFIG_OBJECT_ROLE.ROLEID), newConfigRolesObjects.getRoleId().toString(), actionAuditId, oldConfigRolesObjects.getRoleId().toString());
                }

                if (!newConfigRolesObjects.getStatus().equals(oldConfigRolesObjects.getStatus())) {
                    this.createActionDetail(I18N.get(Constants.CONFIG_OBJECT_ROLE.STATUS), newConfigRolesObjects.getStatus().toString(), actionAuditId, oldConfigRolesObjects.getStatus().toString());
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    public void addLogRole(ConfigRoles newConfigRoles, ConfigRoles oldConfigRoles, Long actionAuditId) {
        try {
            if (oldConfigRoles == null) {
                this.createActionDetail(I18N.get(Constants.CONFIG_ROLE.ROLECODE), newConfigRoles.getRoleCode(), actionAuditId, null);
                this.createActionDetail(I18N.get(Constants.CONFIG_ROLE.ROLENAME), newConfigRoles.getRoleName(), actionAuditId, null);
                this.createActionDetail(I18N.get(Constants.CONFIG_ROLE.ROLEDESCRIPTION), newConfigRoles.getRoleDescription(), actionAuditId, null);
                this.createActionDetail(I18N.get(Constants.CONFIG_ROLE.STATUS), newConfigRoles.getStatus().toString(), actionAuditId, null);
            } else {
                if (!newConfigRoles.getRoleCode().equals(oldConfigRoles.getRoleCode())) {
                    this.createActionDetail(I18N.get(Constants.CONFIG_ROLE.ROLECODE), newConfigRoles.getRoleCode(), actionAuditId, oldConfigRoles.getRoleCode());
                }

                if (!newConfigRoles.getRoleName().equals(oldConfigRoles.getRoleName())) {
                    this.createActionDetail(I18N.get(Constants.CONFIG_ROLE.ROLENAME), newConfigRoles.getRoleName(), actionAuditId, oldConfigRoles.getRoleName());
                }

                if (!newConfigRoles.getRoleDescription().equals(oldConfigRoles.getRoleDescription())) {
                    this.createActionDetail(I18N.get(Constants.CONFIG_ROLE.ROLEDESCRIPTION), newConfigRoles.getRoleDescription(), actionAuditId, oldConfigRoles.getRoleDescription());
                }

                if (!newConfigRoles.getStatus().equals(oldConfigRoles.getStatus())) {
                    this.createActionDetail(I18N.get(Constants.CONFIG_ROLE.STATUS), newConfigRoles.getStatus().toString(), actionAuditId, oldConfigRoles.getStatus().toString());
                }

            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
