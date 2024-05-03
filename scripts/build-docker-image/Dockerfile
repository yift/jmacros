FROM ubuntu:latest

RUN apt update && apt upgrade -y
RUN apt install curl zip unzip -y
RUN useradd -ms /bin/bash gradle
USER gradle
RUN curl -s "https://get.sdkman.io" | bash
USER gradle
RUN /bin/bash -c "source ~/.sdkman/bin/sdkman-init.sh; sdk install gradle"
USER gradle
RUN /bin/bash -c "source ~/.sdkman/bin/sdkman-init.sh; sdk install java 22.0.1-librca"

SHELL ["/bin/bash", "-c"]