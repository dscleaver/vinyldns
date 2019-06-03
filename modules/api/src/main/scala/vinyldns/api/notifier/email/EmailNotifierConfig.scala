/*
 * Copyright 2018 Comcast Cable Communications Management, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package vinyldns.api.notifier.email

import scala.collection.JavaConverters._
import javax.mail.Address
import javax.mail.internet.InternetAddress
import pureconfig.ConfigReader
import scala.util.Try
import pureconfig.error.CannotConvert
import java.util.Properties
import com.typesafe.config.{ConfigObject, ConfigValue}

object EmailNotifierConfig {

  implicit val smtpPropertiesReader: ConfigReader[Properties] = {
    ConfigReader[ConfigObject].map { config =>
      val props = new Properties()
      convertToProperties("mail.smtp", config, props)
      props
    }
  }

  def convertToProperties(baseKey: String, config: ConfigObject, props: Properties): Unit =
    config.keySet().asScala.foreach {
      case key =>
        config.get(key) match {
          case value: ConfigObject =>
            convertToProperties(s"${baseKey}.${key}", value, props)
          case value: ConfigValue =>
            props.put(s"${baseKey}.${key}", value.unwrapped())

        }
    }

  implicit val addressReader: ConfigReader[Address] = ConfigReader[String].emap { s =>
    Try(new InternetAddress(s)).toEither.left.map { exc =>
      CannotConvert(s, "InternetAddress", exc.getMessage())
    }
  }

}

case class EmailNotifierConfig(from: Address, smtp: Properties = new Properties())
