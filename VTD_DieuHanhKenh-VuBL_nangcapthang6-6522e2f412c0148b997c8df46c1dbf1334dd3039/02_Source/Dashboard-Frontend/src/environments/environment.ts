// The file contents for the current environment will overwrite these during build.
// The build system defaults to the dev environment which uses `environment.ts`, but if you do
// `ng build --env=prod` then `environment.prod.ts` will be used instead.
// The list of which env maps to which file can be found in `.angular-cli.json`.

export const environment = {
  production: false,
  url_vsa_change_passwd : 'http://14.225.5.246:8889/passportv3/changePassword',
  // api_url : 'http://103.226.250.30'
  // api_url : 'http://14.225.5.246:8082'
  api_url : 'http://localhost:8082'
};
