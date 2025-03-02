package ru.hse.routemood.gptMessage;

import org.springframework.data.jpa.repository.JpaRepository;

interface GptMessageRepository extends JpaRepository<GptMessage, Long> {

}