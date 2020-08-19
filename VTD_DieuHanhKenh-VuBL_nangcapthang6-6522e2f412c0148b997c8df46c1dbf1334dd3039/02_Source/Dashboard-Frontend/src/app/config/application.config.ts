// const URL_API_BASE_LOCAL = 'http://localhost:8082';
// const URL_BASE = 'http://192.168.8.90:8082';
// const URL_BASE = 'http://localhost:8082';
import {environment} from '../../environments/environment';

const URL_BASE = environment.api_url;


const URL_API_BASE = URL_BASE + '/api';
const DEFAULT_LANGUAGE = 'vi';
export const config = {
  session: 'session_token',
  access_token: 'access_token',
  refresh_token: 'refresh_token',
  token_expired_time: 'token_expired_time',
  token_get_time: 'token_get_time',
  apiUrl: URL_API_BASE,
  apiLogin: URL_BASE + '/oauth/token',
  requestAuthorization: 'Basic ZGFzaGJvYXJkLXdlYi1jbGllbnQ6ZGQ4Y2MxMmM4NjA0NGYwYWZiZDQzYmE3NTY1YzMwNDA=',
  contentType: 'application/x-www-form-urlencoded',
  currentUser: 'currentUser',
  timeoutToast: 3000,
  pageSize: 10,
  plan_monthly_API: URL_API_BASE + '/management/planmonthly/getPlanMonthlyByCondition',
  download_lv2_API: URL_API_BASE + '/dashboard/renderExcelAllStaff',
  productAPI: URL_API_BASE + '/management/product/getByStatus',
  channel_API: URL_API_BASE + '/management/channel/getByStatus',
  group_API: URL_API_BASE + '/management/groupservice/getByStatus',
  serviceChannel_getByServiceId_API: URL_API_BASE + '/management/servicechannel/getByIdService',
  productId_API: URL_API_BASE + '/management/groupservice/getGroupServicesByProductIdAndStatus',
  downloadVDS_API: URL_API_BASE + '/management/planmonthly/downloadVDS',
  uploadFileVDS_API: URL_API_BASE + '/management/planmonthly/uploadVDS',
  download_Result_File_API: URL_API_BASE + '/management/planmonthly/downloadFileFromSystem',
  updateMonthly_API: URL_API_BASE + '/management/planmonthly/updatePlanMonthly',
  updateQuarterly_API: URL_API_BASE + '/management/planmonthly/updatePlanQuarterly',
  updateYearly_API: URL_API_BASE + '/management/planmonthly/updatePlanYearly',
  unitTree_API: URL_API_BASE + '/management/unittree/getAllUnitTrees',
  downloadLevel_API: URL_API_BASE + '/management/planmonthly/downloadLevel',
  uploadFileLevel_API: URL_API_BASE + '/management/planmonthly/uploadLevel',
  unitPartner_API: URL_API_BASE + '/management/partner/getManageInfoPartner',
  unit_level_API: URL_API_BASE + '/management/partner/getManageInfoPartnerLevel',
  unit_dashboard_API: URL_API_BASE + '/management/partner/getManageInfoPartnerDashboard',
  unit_report_API: URL_API_BASE + '/management/partner/findPartnerReport',
  staff_API: URL_API_BASE + '/management/staff/getByShopCodeOfUser',
  dashboard_API: URL_API_BASE + '/dashboard/getDashboard',
  downloadXLSX_Table_API: URL_API_BASE + '/score-service/downloadExcel',
  group_dashboard_API: URL_API_BASE + '/dashboard/getGroups',
  unit_API: URL_API_BASE + '/management/unit/getAllUnit',
  unit_All_API: URL_API_BASE + '/management/unit/getAll',
  unit_search_API: URL_API_BASE + '/management/unit/getByCondition',
  targetGroup_API: URL_API_BASE + '/management/groupservice/getGroupServicebyCondition1',
  insertGroup_API: URL_API_BASE + '/management/groupservice/addNewGroupService',
  updateGroup_API: URL_API_BASE + '/management/groupservice/update',
  warningreceive_getAll_API: URL_API_BASE + '/management/warningreceive/getAll',
  warningreceive_Search_API: URL_API_BASE + '/management/warningreceive/getByCondition',
  warningreceive_add_API: URL_API_BASE + '/management/warningreceive/addwarningreceive',
  warningreceive_update_API: URL_API_BASE + '/management/warningreceive/updatewarningreceive',
  warningreceive_lock_API: URL_API_BASE + '/management/warningreceive/lock',
  warningreceive_unlock_API: URL_API_BASE + '/management/warningreceive/unlock',
  warningreceive_template_API: URL_API_BASE + '/management/warningreceive/downloadTemplate',
  partner_getAll_API: URL_API_BASE + '/service/score/getUnit',
  warningsend_getAllContent_API: URL_API_BASE + '/management/warningcontent/getAll',
  warningsend_getAll_API: URL_API_BASE + '/management/warningsend/getAll',
  warningsend_Search_API: URL_API_BASE + '/management/warningsend/getByCondition',
  getAllService_API: URL_API_BASE + '/management/warningsend/getService',
  warningsend_add_API: URL_API_BASE + '/management/warningsend/addWarningSend',
  warningsend_update_API: URL_API_BASE + '/management/warningsend/updateWarningSend',
  warningsend_template_API: URL_API_BASE + '/management/warningsend/downloadTemplate',
  warningsend_lock_API: URL_API_BASE + '/management/warningsend/lock',
  warningsend_unlock_API: URL_API_BASE + '/management/warningsend/unlock',
  warningconfig_downloadResult_API: URL_API_BASE + '/management/warningsend/downloadResult',
  apparam_getbytype_API: URL_API_BASE + '/management/apparam/getByType',
  apparam_getbytypens_API: URL_API_BASE + '/management/apparam/getByType',
  apparam_getbytypeandstatus_API: URL_API_BASE + '/management/apparam/getByTypeAndStatus',
  active_unit_API: URL_API_BASE + '/management/partner/getActiveUnits',
  sqlQuery_API: URL_API_BASE + '/management/reportSql/getByStatus',
  downloadDetailsReport_API: URL_API_BASE + '/management/reportSql/downloadDetailsReport',
  downloadTemplateReport_API: URL_API_BASE + '/management/reportSql/downloadTemplateReport',
  activeUnit_API: URL_API_BASE + '/management/partner/getActiveUnits',
  defaultLanguage: DEFAULT_LANGUAGE,
  deleteMonthly_API: URL_API_BASE + '/management/planmonthly/deletePlanMonthly',
  deleteQuarterly_API: URL_API_BASE + '/management/planmonthly/deletePlanQuarterly',
  deleteYearly_API: URL_API_BASE + '/management/planmonthly/deletePlanYearly',
  allShop_API: URL_API_BASE + '/dashboard/zoomAllData',
  cycle_VDS_API: URL_API_BASE + '/management/cycle/getByAssign',
  getAllGroup_APi: URL_API_BASE + '/management/groupservice/getAllGroupByProdudct',
  uploadFileGroup_API: URL_BASE + '/api/groupservice/upload',
  getAllWarning_API: URL_BASE + '/api/management/warningconfig/getAllWarning',
  warning_getAllWarningById_API: URL_BASE + '/api/management/warningconfig/getAllWarningById',
  apparam_API: URL_API_BASE + '/management/apparam/getByTypeAndStatus',
  getAllApp_API: URL_API_BASE + '/management/apparam/getAllApparam',
  downloadGroupTemplate_API: URL_API_BASE + '/management/groupservice/downloadTemplate',
  uploadFileGroupServce_API: URL_API_BASE + '/management/groupservice/upLoadFile',
  lockGroup_API: URL_API_BASE + '/management/groupservice/lock',
  unlockGroup_API: URL_API_BASE + '/management/groupservice/unlock',
  mngTarget_weight_API: URL_API_BASE + '/service/score/getServiceScore',
  upload_weight_API: URL_API_BASE + '/service/score/uploadServiceScore',
  create_weight_API: URL_API_BASE + '/service/score/addServiceScore',
  staffs_weight_API: URL_API_BASE + '/service/score/getStaffByShopCode',
  search_weight_API: URL_API_BASE + '/service/score/search',
  update_weight_API: URL_API_BASE + '/service/score/updateServiceScore',
  units_weight_API: URL_API_BASE + '/service/score/getUnit',
  units_VDS_API: URL_API_BASE + '/vds-staff/getUnitVds',
  download_weight_API: URL_API_BASE + '/service/score/downloadServiceScore',
  upload_mngtarget_API: URL_API_BASE + '/management/warningconfig/uploadWarningConfig',
  // object
  object_Update_API: URL_API_BASE + '/management/objectconfig/update',
  object_Save_API: URL_API_BASE + '/management/objectconfig/save',
  object_GetById_API: URL_API_BASE + '/management/objectconfig/getByIdObject/',
  object_GetAllByParentId_API: URL_API_BASE + '/management/objectconfig/getallbyparentid/',
  object_GetAllActive_API: URL_API_BASE + '/management/objectconfig/getAllActive',
  object_GetAllNotDelete_API: URL_API_BASE + '/management/objectconfig/getAllNotDelete',
  object_GetAll_API: URL_API_BASE + '/management/objectconfig/getAll',

  // role-staff
  shop_GetAll_API: URL_API_BASE + '/management/shop/getAll',
  getAllStaff_API: URL_API_BASE + '/management/staff/getAll',
  getSearchStaff_API: URL_API_BASE + '/management/staff/getAllByStaffAndShop',
  getByStaffCode_API: URL_API_BASE + '/management/rolestaff/getRoleByStaffCode',
  // role
  roles_Active: URL_API_BASE + '/management/role/getAllActive',
  roles_API: URL_API_BASE + '/management/role/getAllRole',
  roles_codition_API: URL_API_BASE + '/management/role/searchByCondition',
  getAllGroup_API: URL_API_BASE + '/management/groupservice/getAllGroupServices',
  create_role_API: URL_API_BASE + '/management/role/createRole',
  edit_role_API: URL_API_BASE + '/management/role/editRole',
  getActionOfRole_API: URL_API_BASE + '/management/role/getActionOfRole',
  create_role_object_API: URL_API_BASE + '/management/roleobject/createRoleObject',

  // service
  service_API: URL_API_BASE + '/management/service/getByChannelCodeOfUser',
  downloadService_API: URL_API_BASE + '/management/service/downloadExample',
  downloadGuildEXP_API: URL_API_BASE + '/management/service/downloadGuildEXP',
  uploadFileService_API: URL_API_BASE + '/management/service/uploadService',
  service_getAll_API: URL_API_BASE + '/management/service/getAllService',
  service_CREATE_API: URL_API_BASE + '/management/service/createService',
  createWarning_API: URL_API_BASE + '/management/warningconfig/addWarning',
  editWarning_API: URL_API_BASE + '/management/warningconfig/update',
  groupId_API: URL_API_BASE + '/management/service/getServicesByCondition',
  editService_API: URL_API_BASE + '/management/service/editService',
  getLogOfService_API: URL_API_BASE + '/management/service/getLogOfService',
  getServiceByOrder_API: URL_API_BASE + '/management/service/getServiceByOrder',
  getServiceByStatus_API: URL_API_BASE + '/management/service/getAllServiceByStatus',
  getGroupByStatus: URL_API_BASE + '/management/groupservice/getGroupByStatus',
  getSearchGroupService: URL_API_BASE + '/management/groupservice/search',
  getServiceByID_API: URL_API_BASE + '/management/service/getByServiceID',
  checkServiceParenID_API: URL_API_BASE + '/management/service/checkServiceParenID',

  // warning
  downloadWarnningTemplate_API: URL_API_BASE + '/management/warningconfig/downloadWarningTemplate',

  // GroupVttChannel
  getvttGroupChannelByCondition_API: URL_API_BASE + '/management/vttGroupChannel/getByCondition',
  getActiveVttChannel_API: URL_API_BASE + '/management/vttGroupChannel/getActiveVttChannel',
  addVttGroupChannel_API: URL_API_BASE + '/management/vttGroupChannel/addVttGroupChannel',
  checkCodeConflict_API: URL_API_BASE + '/management/vttGroupChannel/checkCodeDuplicate',
  addVttPosition_API: URL_API_BASE + '/vttPosition/addVttPosition',
  uploadVttPosition_API: URL_API_BASE + '/vttPosition/uploadVttPosition',
  uploadVttGroupChannelSale_API: URL_API_BASE + '/vttGroupChannelSale/uploadVttGroupChannelSale',
  addVttGroupChannelSale_API: URL_API_BASE + '/vttGroupChannelSale/addVttGroupChannelSale',
  getAllNotMapping_API: URL_API_BASE + '/management/vttGroupChannel/getAllNotMapping',

  // ConfigGroupCard
  getAllGroupCard_API: URL_API_BASE + '/management/groupcard/getAll',
  searchGroupCard_API: URL_API_BASE + '/management/groupcard/getByCondition',
  addGroupCard_API: URL_API_BASE + '/management/groupcard/addGroupCard',
  updateGroupCard_API: URL_API_BASE + '/management/groupcard/updateGroupCard',
  deleteGroupCard_API: URL_API_BASE + '/management/groupcard/deleteGroupCard',
  getChannelName_API: URL_API_BASE + '/management/channel/getNameByCode',

  // ConfigSingleCard
  getAllSingleCard_API: URL_API_BASE + '/management/single-card/getAll',
  getActiveCard_API: URL_API_BASE + '/management/single-card/getActiveCard',
  deleteSingleCard_API: URL_API_BASE + '/management/single-card/delete',
  searchSingleCard_API: URL_API_BASE + '/management/single-card/search',
  createSingleCard_API: URL_API_BASE + '/management/single-card/create',
  updateSingleCard_API: URL_API_BASE + '/management/single-card/update',
  getGroupCardOrder_API: URL_API_BASE + '/management/single-card/getGroupCard',

  // COnfigSingleCHart

  getSingleChartByCondition_API: URL_API_BASE + '/configSingleChart/getByCondition',
  addSingleChart_API: URL_API_BASE + '/configSingleChart/addConfigSingleChart',
  updateSingleChart_API: URL_API_BASE + '/configSingleChart/updateConfigSingleChart',
  deleteSingleChart_API: URL_API_BASE + '/configSingleChart/deleteConfigSingleChart',

  // Positon
  getAllPosition_API: URL_API_BASE + '/management/position/getAllPosition',

  // mapping
  getActiveVDSGroupC_API: URL_API_BASE + '/management/channel/getActiveVdsGroupChannel',
  addVDSGroupC_API: URL_API_BASE + '/management/channel/addVdsGroupChannel',

  // shop-unit
  shopUnitByCondition: URL_API_BASE + '/management/shopunit/getByCondition',
  shopUnitSavenew: URL_API_BASE + '/management/shopunit/savenew',
  shopUnitUpdate: URL_API_BASE + '/management/shopunit/update',
  getByID: URL_API_BASE + '/management/shopunit/getById',
  getUnitByID: URL_API_BASE + '/management/unit/getById',
  changeStatus: URL_API_BASE + '/management/shopunit/lockUnlock',
  changeStatusUnit: URL_API_BASE + '/management/unit/lockUnlock',
  saveNewUnit: URL_API_BASE + '/management/unit/add',
  updateNewUnit: URL_API_BASE + '/management/unit/update',
  getAllVdsChannel: URL_API_BASE + '/management/vdsGroupChannel/getAll',

  MAP_CHART_SYMBOL: {
    'circle': '●',
    'diamond': '♦',
    'square': '■',
    'triangle': '▲',
    'triangle-down': '▼',
  },
  user_default_language: 'app_DHK_default_language',


  COLUMN_SERVICE_NAME: {
    'service.': 'Chỉ tiêu'
  },

  // api xây dựng cây điều hành VDS
  search_VDS_API: URL_API_BASE + '/management/partner/getByCondition',
  group_VDS_API: URL_API_BASE + '/management/channel/getChannelByCondition',
  data_declare_VDS_API: URL_API_BASE + '/management/partner/getByCodeOrParentCode',
  create_declare_VDS_API: URL_API_BASE + '/management/partner/addNewManagerInfoPartner',
  search_form_VDS_API: URL_API_BASE + '/management/partner/getByConditionAll',
  update_declare_VDS_API: URL_API_BASE + '/management/partner/updateManagerInfoPartner',
  config_action_VDS_API: URL_API_BASE + '/management/partner/configActive',
  allUnit_VDS_API: URL_API_BASE + '/management/shop/getAllByStatus',
  // check nv da ton tai cho them moi nhan vien vao don vi
  search_VDS_Staff_Exist_API: URL_API_BASE + '/vds-staff/checkStaff',
  //  them moi nhan vien vao don vi vds-staff/create
  search_VDS_Staff_Create_API: URL_API_BASE + '/vds-staff/create',
  // show to table satff vds-staff/show
  search_VDS_Staff_Show_API: URL_API_BASE + '/vds-staff/show',
  // Search staff to add on table
  search_VDS_Staff_Search_API: URL_API_BASE + '/vds-staff/search',
  // Delete staff on table
  search_VDS_Staff_Delete_API: URL_API_BASE + '/vds-staff/delete',
  // update vds Staff
  search_VDS_Staff_Update_API: URL_API_BASE + '/vds-staff/edit',
  // get StaffCode localhost:8082/api/vds-staff/getStaff
  search_VDS_Staff_GetStaff_API: URL_API_BASE + '/vds-staff/getStaff',
  // Up load
  search_VDS_Staff_UpLoad_API: URL_API_BASE + '/vds-staff/upload',

  REPORT_UNIT_SUM_PROVINCE: 'BAO_CAO_TONG_HOP_DON_VI_TINH_TOAN_QUOC',
  REPORT_UNIT_SUM_STAFF: 'BAO_CAO_TONG_HOP_DON_VI_NHAN_VIEN_TOAN_QUOC',

  MONTH_FORMAT: 'MMyyyy',
  DATE_FORMAT: 'yyyyMMdd',
};
