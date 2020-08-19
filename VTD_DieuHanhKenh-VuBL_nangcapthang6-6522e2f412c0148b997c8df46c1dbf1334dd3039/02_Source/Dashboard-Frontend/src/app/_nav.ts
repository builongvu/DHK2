interface NavAttributes {
  [propName: string]: any;
}
interface NavWrapper {
  attributes: NavAttributes;
  element: string;
}
interface NavBadge {
  text: string;
  variant: string;
}
interface NavLabel {
  class?: string;
  variant: string;
}

export interface NavData {
  name?: string;
  url?: string;
  icon?: string;
  img?: string;
  badge?: NavBadge;
  title?: boolean;
  children?: NavData[];
  variant?: string;
  attributes?: NavAttributes;
  divider?: boolean;
  class?: string;
  label?: NavLabel;
  wrapper?: NavWrapper;
}

export const navItems: NavData[] = [
  {
    name: 'Tổng quan',
    url: '/dashboard',
    icon: 'icon-speedometer'
  },
  {
    title: true,
    name: 'Danh mục'
  },
  {
    name: 'Quản lý kênh',
    url: '/channel-management',
    icon: 'icon-user'
  },
  {
    name: 'Chỉ tiêu VDS',
    url: '/vtt-target',
    icon: 'icon-user'
  },
  {
    name: 'Giao chỉ tiêu',
    url: '/vtt-target-level',
    icon: 'icon-user'
  },
  {
    name: 'Quản lý chỉ tiêu',
    url: '/vtt-target-management',
    icon: 'icon-user'
  },
  {
    name: 'Quản lý nhóm chỉ tiêu',
    url: '/vtt-target-group-management',
    icon: 'icon-user'
  }
];
