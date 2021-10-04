FROM openjdk:8-jdk-alpine
WORKDIR /home/Schreibtisch/distri3/proSemDistrSysWS2021/Task3
COPY ./calc_fib.jar /home/Schreibtisch/distri3/proSemDistrSysWS2021/Task3/fib.jar
COPY ./input_full.csv /home/Schreibtisch/distri3/proSemDistrSysWS2021/Task3/input.csv
# make all app files readable (solves issue when dev in Windows, but building in Ubuntu)
RUN chmod -R 755 /home/Schreibtisch/distri3/proSemDistrSysWS2021/Task3/fib.jar
RUN chmod 700 /home/Schreibtisch/distri3/proSemDistrSysWS2021/Task3/fib.jar
EXPOSE 8080
CMD exec java -jar /home/Schreibtisch/distri3/proSemDistrSysWS2021/Task3/fib.jar input.csv
