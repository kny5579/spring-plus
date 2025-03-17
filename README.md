# SPRING PLUS

### 11. Transaction 심화 - log 테이블 결과
![img.png](readmeImg/img.png)
- 매니저 등록 요청을 기록하는 로그 테이블
- 매니저 등록 뿐만 아니라 다른 엔티티에서도 사용하게 될 수 있음을 감안하여 common 패키지에 작성
- 매니저 등록을 실패해도 실패 로그가 저장됩니다.

## 12. AWS 활용

<details>
  <summary>EC2</summary>

#### 탄력적 IP
![ip.png](readmeImg/ip.png)

#### Health Check API
![ec2_hc.png](readmeImg/ec2_hc.png)

---
![ec2_3.png](readmeImg/ec2_3.png)

![ec2_2.png](readmeImg/ec2_2.png)
> ssh를 통해 EC2에 접속한 후 jar 파일 실행

<br>

![ec2_1.png](readmeImg/ec2_1.png)
> 지정된 IP 주소로 접속

<br>

</details>

<br>

<details>
  <summary>RDS</summary>

![rds.png](readmeImg/rds.png)

</details>

<br>

<details>
  <summary>S3</summary>

![s3.png](readmeImg/s3.png)  
> 이미지 업로드 API 정상 실행

<br>

![s3_2.png](readmeImg/s3_2.png)  
> S3 버킷에 저장된 이미지 결과(귀엽죠)

<br>

![s3_3.png](readmeImg/s3_3.png)
> 이미지 삭제 API 정상 실행 -> S3에서 삭제됩니다.

<br>

</details>

<br>


