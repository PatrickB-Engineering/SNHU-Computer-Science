# CS 305: Software Security

**Byrne-Project02.docx** is my Practices for Secure Software Report from Project Two. It covers the refactored Artemis Financial app: HTTPS with a self-signed certificate, a SHA-256 checksum route, a before and after dependency check, and a manual code review.

## The client
Artemis Financial is a consulting company that builds financial plans for its customers, things like savings, retirement, investments, and insurance. They have a public web app and wanted it modernized and secured. Specifically, they wanted a checksum to verify data when it gets transferred, and secure communication between the browser and the server. My job was to find the security problems in their code and refactor it to fix them.

## What I did well, and why it matters
I didn't just run the dependency check and paste the results. In Project One the scan found 252 known vulnerabilities across 11 of their 20 libraries, including four that CISA lists as actively exploited, and I worked through which ones actually mattered and what to do about them. Coding securely matters because Artemis holds people's financial information. A breach means lost money, possible fines under laws like the Gramm-Leach-Bliley Act, and lost customer trust, which is the hardest thing to get back. Security is also a lot cheaper to build in from the start than to bolt on after an attack.

## What was challenging or helpful
False positives were the hard part. The report listed the same Tomcat CVEs twice, once for each Tomcat jar, and flagged some libraries for bugs in code the project doesn't even use. Sorting real from noise took actual reading. The tools also fought me a little. The course's dependency-check version couldn't download data from NVD anymore, so I had to update the plugin and set up an NVD API key. The helpful part was learning to read a CVE entry and compare its affected versions to what's actually in the project.

## Adding layers of security
I added security in layers. The server now runs over HTTPS on port 8443 with a keystore I generated with keytool, so traffic is encrypted. The new /hash route creates a SHA-256 checksum so data can be verified. Then I ran a dependency check and reviewed the code by hand. In the future I'd keep the dependency check in the build so it runs every time, use the vulnerability assessment flow diagram to decide which areas need review, and rank fixes by CVSS score and whether a CVE is on CISA's known exploited list.

## Making sure it worked and stayed secure
I ran the app and loaded https://localhost:8443/hash to confirm the checksum came back. The certificate fingerprint in Chrome matched my server.cer, and Eclipse showed 0 errors. To check whether I introduced anything new, I ran the dependency check before touching the code and again after refactoring. Both scans found the same 254 vulnerabilities in the same 14 libraries, so my changes didn't add any. That makes sense, since my code only uses Java's built-in MessageDigest and no new libraries.

## Tools and practices I'll keep using
The OWASP Dependency-Check Maven plugin, the Java keytool, Java's MessageDigest, Oracle's standard algorithm names list, and the NVD for looking up CVEs. For practices: use built-in, tested crypto instead of writing your own, use SHA-256 instead of broken hashes like MD5 or SHA-1, keep secrets like API keys out of the code, and run a baseline scan before changing anything so you have something to compare against.

## What I'd show an employer
This report shows I can take an existing application, find what's wrong with it, fix it, and prove the fix didn't break anything or add new problems. Employers want to see exactly that, along with being able to explain security findings in plain language to a client.
