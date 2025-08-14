import { createRunOncePlugin } from '@expo/config-plugins'
import { withAndroidPlugin } from './withAndroidPlugin'
import { withIosPlugin } from './withIosPlugin'

const pkg = require('../../package.json')

export default createRunOncePlugin(
  (config) => {
    config = withAndroidPlugin(config)
    config = withIosPlugin(config)
    return config
  },
  pkg.productName,
  pkg.version,
)
