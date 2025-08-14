import { ExpoConfig } from '@expo/config-types'
import { AndroidConfig, withMainApplication } from '@expo/config-plugins'

export function withAndroidPlugin(config: ExpoConfig) {
  config = AndroidConfig.Permissions.withPermissions(config, [
    'android.permissions.CAMERA',
  ])
  return withMainApplication(config, (mod) => {
    const pkgImport = `import br.com.mitra.multibeneficios.vinhedo.FaceTecPackage;`;
    const pkgInit = `new FaceTecPackage()`;

    let contents = mod.modResults.contents;

    // Add import if missing
    if (!contents.includes(pkgImport)) {
      contents = pkgImport + '\n' + contents;
    }

    // Add package to getPackages
    contents = contents.replace(
      /return Arrays\.?<ReactPackage>\.asList\(([^)]*)\)/,
      (match, packages) => {
        if (!packages.includes(pkgInit)) {
          return `return Arrays.<ReactPackage>asList(${packages}, ${pkgInit})`;
        }
        return match;
      }
    );

    mod.modResults.contents = contents;

    return mod;
  }); 
}
