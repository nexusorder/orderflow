import { SHA256, enc } from 'crypto-js'

/**
 * 주어진 문자열에 대해 SHA-256 해시를 계산하는 함수
 * @param input 문자열
 * @returns SHA-256 해시 결과 (16진수 문자열)
 */
export function sha256(
  input: string,
  salt: string = 'EJJxE4eltIGA2c78zxboCQHw'
): string {
  const hash = SHA256(input + salt) // SHA-256 해시 계산
  return hash.toString(enc.Hex) // 해시를 16진수 문자열로 변환
}
