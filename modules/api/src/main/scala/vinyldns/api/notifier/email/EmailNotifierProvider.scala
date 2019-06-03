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

import vinyldns.core.notifier.{Notifier, NotifierConfig, NotifierProvider}
import vinyldns.core.domain.membership.UserRepository
import pureconfig.module.catseffect.loadConfigF
import cats.effect.IO
import javax.mail.Session

class EmailNotifierProvider extends NotifierProvider {

  import EmailNotifierConfig._

  def load(config: NotifierConfig, userRepository: UserRepository): IO[Notifier] =
    for {
      emailConfig <- loadConfigF[IO, EmailNotifierConfig](config.settings)
      session <- createSession(emailConfig)
    } yield new EmailNotifier(emailConfig, session, userRepository)

  def createSession(config: EmailNotifierConfig): IO[Session] = IO {
    println(config.smtp.getProperty("mail.smtp.auth.login.disable"))
    Session.getInstance(config.smtp)
  }

}
